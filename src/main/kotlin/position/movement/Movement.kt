package nl.komenzie.cableCam.position.movement

import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.geometry.Point
import kotlin.time.Duration

abstract class Movement(
    val cPosStart: Point,
    val cPosEnd: Point,
    val startTime: Duration,
    val speed: Double,
    val acceleration: Double,
) {
    abstract val totalTime: Duration
    val endTime = startTime + totalTime

    /**
     * @param relativeTime The time progression that needs to be processed (currentTime - startTime)
     * @return The point on which the cPos should be on that time instance
     */
    abstract fun calculateDesiredCartStateRelative(relativeTime: Duration): CartState

    fun calculatedDesiredCartState(currentTime: Duration): CartState {
        return calculateDesiredCartStateRelative(currentTime - startTime)
    }
}
