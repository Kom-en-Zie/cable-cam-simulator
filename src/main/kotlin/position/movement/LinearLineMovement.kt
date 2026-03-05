package nl.komenzie.cableCam.position.movement

import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.util.time.toSeconds
import kotlin.math.pow
import kotlin.time.Duration

class LinearLineMovement(
    cPosStart: Point,
    cPosEnd: Point,
    startTime: Duration,
    speed: Double,
    acceleration: Double,
) : Movement(cPosStart, cPosEnd, startTime, speed, acceleration) {
    val accelerationDistance = speed.pow(2) / (2 * acceleration)
    val accelerationTime = speed / acceleration

    override fun calculateDesiredCartState(relativeTime: Duration): CartState {
        TODO("Not yet implemented")
    }

    private fun calculateDistanceToStart(relativeTime: Duration): Double {
        val time = relativeTime.toSeconds()
        if (time > accelerationTime) {
            return speed * (time - accelerationTime) + accelerationDistance
        }
        TODO("Distance when still accelerating")
    }
}
