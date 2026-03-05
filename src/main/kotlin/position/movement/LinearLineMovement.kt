package nl.komenzie.cableCam.position.movement

import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.MovementVector
import nl.komenzie.cableCam.util.time.toSeconds
import kotlin.math.pow
import kotlin.math.sqrt
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
    val track = Line(cPosStart, cPosEnd)
    val trackLength = track.length
    val topSpeedTravelingDistance = trackLength - 2 * accelerationDistance
    val topSpeedTravelingTime = topSpeedTravelingDistance / speed

    override fun calculateDesiredCartState(relativeTime: Duration): CartState {
        val desiredPos = cPosStart + calculateRelativePos(relativeTime)
        val desiredMovementVector = MovementVector(track.angle, TODO("Speed at this relativeTime"))
        return CartState(desiredPos, desiredMovementVector)
    }

    private fun calculateRelativePos(relativeTime: Duration): Point {
        // c = sqrt (a^2 + b^2) with a=b -> c = sqrt(2) * a -> a = c / sqrt(2)
        val traveledXandY = calculateDistanceToStart(relativeTime) / sqrt(2.0)
        return Point(traveledXandY, traveledXandY)
    }

    private fun calculateDistanceToStart(relativeTime: Duration): Double {
        val time = relativeTime.toSeconds()
        // Still accelerating
        if (time < accelerationTime) return calculateAcceleratingDistance(time)

        // Traveling at top speed (before required deceleration)
        val traveledDistance = speed * (time - accelerationTime) + accelerationDistance
        if (traveledDistance <= trackLength - accelerationDistance) return traveledDistance

        // Currently decelerating
        val deceleratingTime = time - accelerationTime - topSpeedTravelingTime
        val deceleratingDistance =
            accelerationDistance - calculateAcceleratingDistance(accelerationTime - deceleratingTime)
        return accelerationDistance + topSpeedTravelingDistance + deceleratingDistance
    }

    private fun calculateAcceleratingDistance(relativeTime: Double): Double {
        return 0.5 * acceleration * relativeTime.pow(2)
    }
}
