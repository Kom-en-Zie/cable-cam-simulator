package nl.komenzie.cableCam.parts.motors

import kotlinx.serialization.Serializable
import nl.komenzie.cableCam.util.time.toSeconds
import kotlin.time.Duration

/**
 * @param speed The Current speed (m/s) of cable input/output. Pulling cable in is a negative number
 * @param properties The properties of this motor
 */
@Serializable
class MotorState(
    val properties: MotorProperties,
    var speed: Double,
) {
    fun getPassedCableLength(deltaTime: Duration): Double {
        return speed * deltaTime.toSeconds()
    }
}
