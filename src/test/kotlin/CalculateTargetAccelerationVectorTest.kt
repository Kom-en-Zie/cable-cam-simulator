package nl.komenzie.cableCam

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import nl.komenzie.cableCam.cartState.CartConfig
import nl.komenzie.cableCam.cartState.getDesiredState
import nl.komenzie.cableCam.cartState.translationLayer.calculateMotorAccelerations
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.targetVector.calculateTargetAccelerationVector
import nl.komenzie.cableCam.parts.motors.MotorProperties
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.position.movement.LinearLineMovement
import nl.komenzie.cableCam.time.TimeState
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * End-to-end regression tests for `calculateTargetAccelerationVector`'s closed loop:
 * a critically-damped PD controller on the gap between actual (position, velocity) and
 * `getDesiredState()`'s current (position, velocity) snapshot — nothing about the
 * movement queue's internals, and nothing about where the desired state is *going*.
 * That gives it two guarantees worth protecting with tests:
 *  - it converges without oscillating (critical damping is exact, not tuned by feel),
 *  - while actively tracking a moving target it lags by only a small, roughly constant
 *    amount (proportional to the target's acceleration divided by the position gain),
 *    not one that grows with target speed or distance travelled.
 *
 * `MotorProperties`' maxAcceleration here matches Main.kt's actual value (15.0) — with
 * the old, much lower 3.5 these gains keep the motors saturated long enough for actual
 * speed to build up until a 1-second position projection lands outside the physically
 * valid cable-length region (a real crash, not just a slow-convergence artifact).
 */
class CalculateTargetAccelerationVectorTest {
    private fun freshState() = CableCamState(
        Point(40.0, 5.0),
        .75, .40, 3.5,
        75.0, 110.0,
        MotorState(MotorProperties(1500.0, 15.0), 0.0),
        MotorState(MotorProperties(1500.0, 15.0), 0.0),
        CartConfig(maxSpeed = 25.0, maxAcceleration = 2.0),
        TimeState(),
    )

    private fun enqueue(state: CableCamState, from: Point, to: Point) {
        state.movementQueue.add(
            LinearLineMovement(
                cPosStart = from,
                cPosEnd = to,
                startTime = state.timeState.timePassed,
                speed = state.cartConfig.maxSpeed,
                acceleration = state.cartConfig.maxAcceleration - 1.0,
            )
        )
    }

    private fun tick(state: CableCamState, ticks: Int, dt: Duration): Double {
        val dtSeconds = dt.inWholeMilliseconds / 1000.0
        var maxLagWhileActive = 0.0
        repeat(ticks) {
            val (a1, a2) = state.calculateMotorAccelerations()
            state.motor1State.speed += a1 * dtSeconds
            state.motor2State.speed += a2 * dtSeconds
            state.update(dt)
            val desired = state.getDesiredState()
            if (desired != null && desired.movementVector.speed > 0.1) {
                maxLagWhileActive = maxOf(maxLagWhileActive, Line(state.cPos, desired.position).length)
            }
        }
        return maxLagWhileActive
    }

    @Test
    fun convergesWithoutOscillationAndWithImperceptibleLag() {
        val state = freshState()
        val target = Point(10.0, -5.0)
        enqueue(state, state.cPos, target)

        val maxLagWhileActive = tick(state, 3000, 10.milliseconds) // 30s

        val finalDistance = Line(state.cPos, target).length
        assertTrue(
            finalDistance < 1e-6,
            "expected the cart to settle essentially exactly on the target, got distance=$finalDistance",
        )
        assertTrue(
            maxLagWhileActive < 0.02,
            "expected lag while actively tracking to stay imperceptibly small (<2cm), got $maxLagWhileActive",
        )
    }

    @Test
    fun convergesTheSameWayOnAMuchLargerMove() {
        // Lag should stay small even far from the starting point, since it's driven by
        // the target's acceleration (a per-project constant), not by distance travelled.
        val state = freshState()
        val target = Point(35.0, -2.0)
        enqueue(state, state.cPos, target)

        val maxLagWhileActive = tick(state, 6000, 10.milliseconds) // 60s

        val finalDistance = Line(state.cPos, target).length
        assertTrue(finalDistance < 1e-6, "expected exact settling, got distance=$finalDistance")
        assertTrue(maxLagWhileActive < 0.02, "expected imperceptible lag on a large move too, got $maxLagWhileActive")
    }

    @Test
    fun reinputtingTheSameTargetAfterReachingItStaysSettled() {
        val state = freshState()
        val target = Point(10.0, -5.0)
        enqueue(state, state.cPos, target)
        tick(state, 3000, 10.milliseconds)

        val distanceAfterFirstApproach = Line(state.cPos, target).length
        assertTrue(distanceAfterFirstApproach < 1e-6)

        // Mirrors Main.kt's stdin loop: a repeated identical input chains from
        // lastQueuedEnd == target, producing a zero-length LinearLineMovement.
        enqueue(state, target, target)
        tick(state, 500, 10.milliseconds)

        val finalDistance = Line(state.cPos, target).length
        assertTrue(
            finalDistance < 1e-6,
            "expected re-queuing the same target to leave the cart settled exactly on it " +
                "(was $distanceAfterFirstApproach, now $finalDistance)",
        )
        assertTrue(
            abs(state.motor1State.speed) < 0.01 && abs(state.motor2State.speed) < 0.01,
            "expected motor speeds to stay essentially at rest, not spike from a degenerate re-queued movement " +
                "(motor1=${state.motor1State.speed}, motor2=${state.motor2State.speed})",
        )
    }

    /**
     * Proves the "independent of the future of the desired state" requirement directly:
     * two movements with wildly different endpoints (and therefore different total
     * trip times, top speeds sustained, and deceleration points — different "futures")
     * but identical (speed, acceleration) produce the exact same (position, velocity)
     * one second in, while both are still in their acceleration ramp. The controller
     * must produce identical output for both, since it only ever reads that snapshot.
     */
    @Test
    fun outputDependsOnlyOnCurrentDesiredSnapshotNotOnWhereItsHeaded() {
        val nearState = freshState()
        val farState = freshState()
        val start = nearState.cPos

        // Both: speed=10, acceleration=5 -> accelerationTime = 10/5 = 2s, so at t=1s
        // both are still purely in the "still accelerating" branch, which depends only
        // on (speed, acceleration, relativeTime) -- not on cPosEnd/trackLength at all.
        nearState.movementQueue.add(
            LinearLineMovement(
                cPosStart = start, cPosEnd = Point(start.x + 100.0, start.y),
                startTime = nearState.timeState.timePassed, speed = 10.0, acceleration = 5.0,
            )
        )
        farState.movementQueue.add(
            LinearLineMovement(
                cPosStart = start, cPosEnd = Point(start.x + 200.0, start.y),
                startTime = farState.timeState.timePassed, speed = 10.0, acceleration = 5.0,
            )
        )

        nearState.update(1.seconds)
        farState.update(1.seconds)

        val nearDesired = nearState.getDesiredState()!!
        val farDesired = farState.getDesiredState()!!
        assertEquals(nearDesired.position, farDesired.position, "test setup should give identical snapshots")
        assertEquals(nearDesired.movementVector.speed, farDesired.movementVector.speed, 1e-9, "test setup should give identical snapshots")

        val nearAccel = nearState.calculateTargetAccelerationVector()
        val farAccel = farState.calculateTargetAccelerationVector()

        assertEquals(nearAccel.acceleration, farAccel.acceleration, 1e-9)
        assertEquals(nearAccel.angle.radians, farAccel.angle.radians, 1e-9)
    }
}
