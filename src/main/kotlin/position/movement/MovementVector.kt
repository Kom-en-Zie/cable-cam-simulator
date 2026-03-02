package nl.komenzie.cableCam.position.movement

import kotlinx.serialization.Serializable
import nl.komenzie.cableCam.geometry.Angle
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.util.time.toSeconds
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration

/**
 * @param angle The angle of the movement vector in degrees
 * @param speed The speed in m/s
 */
@Serializable
class MovementVector(
    val angle: Angle,
    val speed: Double,
) {
    fun newPos(startPos: Point, time: Duration): Point {
        val distance = speed * time.toSeconds()
        val dx = distance * cos(angle.radians)
        val dy = distance * sin(angle.radians)
        return Point(startPos.x + dx, startPos.y + dy)
    }
}
