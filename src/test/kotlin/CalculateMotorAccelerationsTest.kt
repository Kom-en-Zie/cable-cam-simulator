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
import kotlin.time.Duration.Companion.milliseconds

/**
 * Regression test for a bug where the cart moved with roughly the right speed but in
 * the wrong direction: `calculateT1T2` (which `calculateMotorAccelerations` uses, via
 * `toTChangeFactors`, to decide which way to move the motors) ignored `aPos`, so it
 * disagreed with the real, aPos-dependent circle-intersection physics in
 * `calculateCPos`. This drives the same closed loop `SimulatedMotorDriver` and
 * `Main.kt`'s physics thread run together, but deterministically (no real threads/
 * sleeps), so it exercises the same production code without timing flakiness.
 */
class CalculateMotorAccelerationsTest {
    private fun freshState() = CableCamState(
        Point(40.0, 5.0),
        .75, .40, 3.5,
        75.0, 110.0,
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        CartConfig(maxSpeed = 25.0, maxAcceleration = 5.0),
        TimeState(),
    )

    private fun driveTowards(state: CableCamState, target: Point, ticks: Int, dt: kotlin.time.Duration) {
        state.movementQueue.add(
            LinearLineMovement(
                cPosStart = state.cPos,
                cPosEnd = target,
                startTime = state.timeState.timePassed,
                speed = state.cartConfig.maxSpeed,
                acceleration = state.cartConfig.maxAcceleration,
            )
        )
        // Advance past t=0, where the desired speed is still 0 too (movement hasn't
        // accelerated yet), so there is an actual gap for the driver to close.
        state.update(50.milliseconds)

        val dtSeconds = dt.inWholeMilliseconds / 1000.0
        repeat(ticks) {
            val (a1, a2) = state.calculateMotorAccelerations()
            state.motor1State.speed += a1 * dtSeconds
            state.motor2State.speed += a2 * dtSeconds
            state.update(dt)
        }
    }

    /** Reproduces the exact scenario reported: a small move to (15, -9) from Main.kt's starting t1/t2. */
    @Test
    fun closesInOnSmallDiagonalTarget() {
        val state = freshState()
        val target = Point(15.0, -9.0)
        val initialDistance = Line(state.cPos, target).length

        driveTowards(state, target, ticks = 30, dt = 10.milliseconds)

        val finalDistance = Line(state.cPos, target).length
        assertTrue(
            finalDistance < initialDistance,
            "expected the cart to move closer to the target (was $initialDistance, now $finalDistance)",
        )
    }

    @Test
    fun closesInOnLargerTarget() {
        val state = freshState()
        val target = Point(state.cPos.x + 20.0, state.cPos.y - 12.0)
        val initialDistance = Line(state.cPos, target).length

        driveTowards(state, target, ticks = 30, dt = 10.milliseconds)

        val finalDistance = Line(state.cPos, target).length
        assertTrue(
            finalDistance < initialDistance,
            "expected the cart to move closer to the target (was $initialDistance, now $finalDistance)",
        )
    }
}
