package nl.komenzie.cableCam.position.movement

import nl.komenzie.cableCam.geometry.Point
import kotlin.time.Duration

class LinearLineMovement(
    cPosStart: Point,
    cPosEnd: Point,
    startTime: Duration,
    speed: Double,
    acceleration: Double,
) : Movement(cPosStart, cPosEnd, startTime, speed, acceleration) {

    override fun changePropertiesRelative(relativeTime: Duration): Point {
        TODO("Not yet implemented")
    }
}
