package nl.komenzie.cableCam

import kotlin.test.Test
import kotlin.test.assertTrue
import nl.komenzie.cableCam.cartState.CartConfig
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorProperties
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.parts.motors.driver.SimulatedMotorDriver
import nl.komenzie.cableCam.position.movement.LinearLineMovement
import nl.komenzie.cableCam.time.TimeState
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

class SimulatedMotorDriverTest {
    private fun freshState() = CableCamState(
        Point(40.0, 5.0),
        .75, .40, 3.5,
        75.0, 110.0,
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        CartConfig(maxSpeed = 25.0, maxAcceleration = 5.0),
        TimeState(),
    )

    @Test
    fun drivesMotorSpeedsTowardsQueuedTarget() {
        val state = freshState()
        state.movementQueue.add(
            LinearLineMovement(
                cPosStart = state.cPos,
                cPosEnd = Point(state.cPos.x + 20.0, state.cPos.y - 12.0),
                startTime = state.timeState.timePassed,
                speed = state.cartConfig.maxSpeed,
                acceleration = state.cartConfig.maxAcceleration,
            )
        )
        // Advance past t=0, where the desired speed is still 0 too (movement hasn't
        // accelerated yet), so there is an actual gap for the driver to close.
        state.update(50.milliseconds)

        val driver = SimulatedMotorDriver(state, controlInterval = 5.milliseconds)
        driver.start()
        try {
            Thread.sleep(100)
        } finally {
            driver.stop()
        }

        assertTrue(state.motor1State.speed.isFinite() && state.motor2State.speed.isFinite())
        assertTrue(
            state.motor1State.speed != 0.0 || state.motor2State.speed != 0.0,
            "expected the driver to move at least one motor off its resting speed",
        )
        // Sanity bound: the driver can't apply more than each motor's maxAcceleration,
        // so with generous headroom for scheduling jitter it shouldn't run away.
        assertTrue(abs(state.motor1State.speed) <= state.motor1State.properties.maxAcceleration * 0.5)
        assertTrue(abs(state.motor2State.speed) <= state.motor2State.properties.maxAcceleration * 0.5)
    }
}
