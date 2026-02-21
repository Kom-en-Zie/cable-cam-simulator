package nl.komenzie.cableCam.parts.motors

import kotlinx.serialization.Serializable

/**
 * @param maxPower The maximum power (W) this motor can output
 * @param maxAcceleration The maximum acceleration (m/s^2) this motor can change its output speed
 */
@Serializable
data class MotorProperties(
    val maxPower: Double,
    val maxAcceleration: Double,
)
