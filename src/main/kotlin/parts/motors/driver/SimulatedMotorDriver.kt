package nl.komenzie.cableCam.parts.motors.driver

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.cartState.translationLayer.calculateMotorAccelerations
import nl.komenzie.cableCam.util.time.toSeconds
import java.lang.Thread.sleep
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

/**
 * Stands in for a real machine driving the physical motors: on its own thread it repeatedly
 * computes each motor's target acceleration and integrates it into [nl.komenzie.cableCam.parts.motors.MotorState.speed]
 * (m/s of cable, positive rolling out) — the same interface a real, USB-driven machine would fulfil.
 */
class SimulatedMotorDriver(
    private val cableCamState: CableCamState,
    private val controlInterval: Duration = 10.milliseconds,
) : MotorDriver {

    @Volatile
    private var running = false
    private var thread: Thread? = null

    override fun start() {
        if (running) return
        running = true

        thread = Thread {
            val intervalJavaDuration = controlInterval.toJavaDuration()
            val intervalSeconds = controlInterval.toSeconds()

            while (running) {
                val (motor1Acceleration, motor2Acceleration) = cableCamState.calculateMotorAccelerations()
                cableCamState.motor1State.speed += motor1Acceleration * intervalSeconds
                cableCamState.motor2State.speed += motor2Acceleration * intervalSeconds

                sleep(intervalJavaDuration)
            }
        }.apply {
            isDaemon = true
            name = "simulated-motor-driver"
        }
        thread?.start()
    }

    override fun stop() {
        running = false
        thread?.join()
        thread = null
    }
}
