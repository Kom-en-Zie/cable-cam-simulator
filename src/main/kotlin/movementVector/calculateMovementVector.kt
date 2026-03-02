package nl.komenzie.cableCam.movementVector

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.position.calculateCPos
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun CableCamState.calculateMovementVector(): MovementVector {
    val secondDuration = 1.toDuration(DurationUnit.SECONDS)
    val progressedT1 = this.t1 + this.motor1State.getPassedCableLength(secondDuration)
    val progressedT2 = this.t2 + this.motor2State.getPassedCableLength(secondDuration)
    val progressedCPos = this.calculateCPos(progressedT1, progressedT2)

    val distanceX = abs(progressedCPos.x - this.cPos.x)
    val distanceY = abs(progressedCPos.y - this.cPos.y)
    val distance = sqrt(distanceX.pow(2) + distanceY.pow(2))

    return MovementVector(
        angle = Line(this.cPos, progressedCPos).angle,
        speed = distance,   // m/s with s=1
    )
}
