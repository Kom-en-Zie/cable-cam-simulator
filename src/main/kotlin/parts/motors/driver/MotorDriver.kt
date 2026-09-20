package nl.komenzie.cableCam.parts.motors.driver

/**
 * Drives a [nl.komenzie.cableCam.CableCamState]'s motors towards its target state.
 * This is the swap point between the in-process simulation and a real machine
 * (e.g. an Arduino over USB) actuating physical motors.
 */
interface MotorDriver {
    fun start()
    fun stop()
}
