package nl.komenzie.cableCam.movementVector.targetVector

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.cartState.getDesiredState
import nl.komenzie.cableCam.geometry.Angle
import nl.komenzie.cableCam.movementVector.AccelerationVector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

// Claude Code did a lot of tests and these numbers functioned best
private const val POSITION_GAIN = 400.0
private const val VELOCITY_GAIN = 40.0

fun CableCamState.calculateTargetAccelerationVector(): AccelerationVector {
    val desiredState = getDesiredState() ?: currentCartState

    val desiredVx = desiredState.movementVector.speed * cos(desiredState.movementVector.angle.radians)
    val desiredVy = desiredState.movementVector.speed * sin(desiredState.movementVector.angle.radians)
    val actualVx = movementVector.speed * cos(movementVector.angle.radians)
    val actualVy = movementVector.speed * sin(movementVector.angle.radians)

    val accelX = POSITION_GAIN * (desiredState.position.x - cPos.x) + VELOCITY_GAIN * (desiredVx - actualVx)
    val accelY = POSITION_GAIN * (desiredState.position.y - cPos.y) + VELOCITY_GAIN * (desiredVy - actualVy)

    return AccelerationVector(Angle(atan2(accelY, accelX)), hypot(accelX, accelY))
}
