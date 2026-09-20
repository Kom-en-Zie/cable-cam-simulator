package nl.komenzie.cableCam.movementVector.targetVector

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.movementVector.AccelerationVector
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun CableCamState.calculateTargetAccelerationVector(): AccelerationVector {
    val movementVector = this.movementVector
    val targetVector = this.calculateTargetVector()

    val secondDuration = 1.toDuration(DurationUnit.SECONDS)

    // Create a (acceleration) vector from the end of the movement vector to the end op the target vector (and return it)
    val endOfMovement = movementVector.newPos(this.cPos, secondDuration)
    val endOfTarget = targetVector.newPos(this.cPos, secondDuration)
    val vectorLine = Line(endOfMovement, endOfTarget)
    val accelerationVector = AccelerationVector(
        vectorLine.angle,
        vectorLine.length,  // m/s^2 over a second
    )

    return accelerationVector
}