package nl.komenzie.cableCam.position.movement

import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.MovementVector
import kotlin.time.Duration

abstract class Movement(
    val cPosStart: Point,
    val cPosEnd: Point,
    val initialMovement: MovementVector,
    val startTime: Duration,
    val speed: Double,
    val acceleration: Double,
) {

    /**
     * @param relativeTime The time progression that needs to be processed (currentTime - startTime)
     * @return The point on which the cPos should be on that time instance
     */
    abstract fun calculateDesiredPositionRelative(relativeTime: Duration): Point

    fun calculatedDesiredPosition(currentTime: Duration): Point {
        return calculateDesiredPositionRelative(currentTime - startTime)
    }
}
