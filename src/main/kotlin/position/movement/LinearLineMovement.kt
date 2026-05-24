package nl.komenzie.cableCam.position.movement

import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.MovementVector
import nl.komenzie.cableCam.util.time.toSeconds
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
    override val totalTime = (2 * accelerationTime + topSpeedTravelingTime).toDuration(DurationUnit.SECONDS)

    override fun calculateDesiredCartStateRelative(relativeTime: Duration): CartState {
        if (relativeTime >= totalTime) return CartState(cPosEnd, MovementVector(track.angle, 0.0))

        val desiredPos = cPosStart + calculateRelativePos(relativeTime)
        val desiredMovementVector = MovementVector(track.angle, calculateSpeed(relativeTime))

        return CartState(desiredPos, desiredMovementVector)
    }

    private fun calculateRelativePos(relativeTime: Duration): Point {
        val distance = calculateDistanceToStart(relativeTime)
        val theta = track.angle.radians
        return Point(distance * cos(theta), distance * sin(theta))
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

    private fun calculateSpeed(relativeTime: Duration): Double {
        val time = relativeTime.toSeconds()
        // Traveling at top speed
        if (time >= accelerationTime && time <= accelerationTime + topSpeedTravelingTime) return speed

        // Still accelerating
        if (time < accelerationTime) return time * acceleration

        // Currently decelerating
        if (time > topSpeedTravelingTime + accelerationTime && relativeTime <= totalTime) {
            val deceleratingTime = time - accelerationTime - topSpeedTravelingTime
            val acceleratingTimeEquivalent = accelerationTime - deceleratingTime
            return acceleratingTimeEquivalent * acceleration
        }

        return 0.0  // Already at end
    }
}
