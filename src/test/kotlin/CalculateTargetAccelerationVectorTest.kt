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
 * End-to-end regression tests for the closed loop actually converging on a queued
 * target: settling without runaway divergence, and not spiking off after a duplicate
 * identical input (which chains into a degenerate, zero-length `LinearLineMovement`).
 *
 * The thresholds here are deliberately tight — tightened specifically to catch a bug
 * where `calculateMotorAccelerations` multiplied the *normalized* `changeT1Factor`/
 * `changeT2Factor` (which sum to 1 by absolute value) directly by an absolute
 * acceleration magnitude, instead of scaling back up by `totalChangeMagnitude` first.
 * That silently applied only a fraction of the intended acceleration (a fraction that
 * varies by direction and position), which on this exact scenario left the cart
 * settling ~0.018 units off target instead of converging tightly. See
 * `ToTChangeFactorsTest` for a unit-level check of the same fix.
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

        tick(state, 3000, 10.milliseconds) // 30s

        val finalDistance = Line(state.cPos, target).length
        assertTrue(
            finalDistance < 0.01,
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
        assertTrue(distanceAfterFirstApproach < 0.01)

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
