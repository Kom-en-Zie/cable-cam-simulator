package nl.komenzie.cableCam

import kotlin.test.Test
import kotlin.test.assertTrue
import nl.komenzie.cableCam.cartState.CartConfig
import nl.komenzie.cableCam.cartState.translationLayer.calculateMotorAccelerations
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorProperties
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.position.movement.LinearLineMovement
import nl.komenzie.cableCam.time.TimeState
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Regression tests for a bug where `calculateTargetVector` extended the position-gap
 * correction along `desiredState.movementVector.angle` — the movement queue's own
 * per-segment track angle. That's only meaningful while a movement is actively
 * playing, and caused two symptoms:
 *  - overshoot that never got braked, because the "target" kept extending further
 *    along the original track direction even once the cart had already passed it;
 *  - a wild direction change after re-queuing the exact same point the cart had just
 *    reached, since that produces a zero-length movement whose `track.angle` is the
 *    arbitrary `atan2(0,0) = 0`.
 * The fix uses the gap's own direction (`Line(cPos, desiredState.position).angle`)
 * instead — a resting desired state always has speed 0, so this changes nothing while
 * a movement is actively tracking well, but fixes both broken cases exactly. The
 * control law is otherwise unchanged, so it's still underdamped/oscillatory before it
 * settles — these tests allow for that rather than asserting fast, tight convergence.
 */
class CalculateTargetAccelerationVectorTest {
    private fun freshState() = CableCamState(
        Point(40.0, 5.0),
        .75, .40, 3.5,
        75.0, 110.0,
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        CartConfig(maxSpeed = 25.0, maxAcceleration = 5.0),
        TimeState(),
    )

    private fun enqueue(state: CableCamState, from: Point, to: Point) {
        state.movementQueue.add(
            LinearLineMovement(
                cPosStart = from,
                cPosEnd = to,
                startTime = state.timeState.timePassed,
                speed = state.cartConfig.maxSpeed,
                acceleration = state.cartConfig.maxAcceleration,
            )
        )
        // Advance past t=0, where the desired speed is still 0 too (movement hasn't
        // accelerated yet), so there is an actual gap for the driver to close.
        state.update(50.milliseconds)
    }

    private fun tick(state: CableCamState, ticks: Int, dt: Duration) {
        val dtSeconds = dt.inWholeMilliseconds / 1000.0
        repeat(ticks) {
            val (a1, a2) = state.calculateMotorAccelerations()
            state.motor1State.speed += a1 * dtSeconds
            state.motor2State.speed += a2 * dtSeconds
            state.update(dt)
        }
    }

    @Test
    fun convergesWithoutRunawayOvershoot() {
        val state = freshState()
        val target = Point(10.0, -5.0)
        enqueue(state, state.cPos, target)

        // 30s: this control law is underdamped and takes a while to settle, but with
        // the fix it does settle — the bug it regresses against made it diverge
        // instead (still ~4+ units away and moving further off after this long).
        tick(state, 3000, 10.milliseconds)

        val finalDistance = Line(state.cPos, target).length
        assertTrue(
            finalDistance < 0.3,
            "expected the cart to settle close to the target, got distance=$finalDistance",
        )
    }

    @Test
    fun reinputtingTheSameTargetAfterReachingItStaysSettled() {
        val state = freshState()
        val target = Point(10.0, -5.0)
        enqueue(state, state.cPos, target)
        tick(state, 3000, 10.milliseconds)

        val distanceAfterFirstApproach = Line(state.cPos, target).length
        assertTrue(distanceAfterFirstApproach < 0.3)

        // Mirrors Main.kt's stdin loop: a repeated identical input chains from
        // lastQueuedEnd == target, producing a zero-length LinearLineMovement.
        enqueue(state, target, target)
        tick(state, 500, 10.milliseconds)

        val finalDistance = Line(state.cPos, target).length
        assertTrue(
            finalDistance < 0.5,
            "expected re-queuing the same target to leave the cart settled near it " +
                "(was $distanceAfterFirstApproach, now $finalDistance)",
        )
        assertTrue(
            abs(state.motor1State.speed) < 1.0 && abs(state.motor2State.speed) < 1.0,
            "expected motor speeds to stay small, not spike from a degenerate re-queued movement " +
                "(motor1=${state.motor1State.speed}, motor2=${state.motor2State.speed})",
        )
    }
}
