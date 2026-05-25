package nl.komenzie.cableCam.movementVector

import kotlinx.serialization.Serializable
import nl.komenzie.cableCam.geometry.Angle
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.util.time.toSeconds
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration

/**
 * @param angle The angle of the movement vector in degrees
 * @param acceleration The speed in m/s
 */
@Serializable
class AccelerationVector(
    val angle: Angle,
    val acceleration: Double,
) {
}