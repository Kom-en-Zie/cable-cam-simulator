package nl.komenzie.cableCam.movementVector.inBetweenVector

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.cartState.getDesiredState
import nl.komenzie.cableCam.constants.DT
import nl.komenzie.cableCam.geometry.Angle
import nl.komenzie.cableCam.movementVector.AccelerationVector
import nl.komenzie.cableCam.movementVector.MovementVector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun CableCamState.inBetweenVector(): AccelerationVector {
    val carPosx = this.currentCartState.position.x
    val carPosy = this.currentCartState.position.y

    val desiredState = this.getDesiredState()
    if (desiredState == null) {
        return AccelerationVector(Angle(0.0), 0.0)
    }
    val desiredPosx = desiredState.position.x
    val desiredPosy = desiredState.position.y

    val desiredAngleRad = desiredState.movementVector.angle.radians
    val desiredSpeed = desiredState.movementVector.speed

    val goalPosx = desiredPosx + cos(desiredAngleRad) * desiredSpeed * DT
    val goalPosy = desiredPosy + sin(desiredAngleRad) * desiredSpeed * DT

    val movementAngle = atan2(goalPosy - carPosy, goalPosx - carPosx)

    val deltaX = goalPosx - carPosx
    val deltaY = goalPosy - carPosy
    val currentError = sqrt((deltaX * deltaX) + (deltaY * deltaY))

    val Kp = 0.5
    val Ki = 0.01
    val Kd = 0.1

    val pTerm = Kp * currentError

    this.integralError += currentError * DT
    val iTerm = Ki * this.integralError

    val derivative = (currentError - this.lastError) / DT
    val dTerm = Kd * derivative

    this.lastError = currentError

    val calculatedAcceleration = pTerm + iTerm + dTerm


    return AccelerationVector(Angle(movementAngle), calculatedAcceleration)



    TODO()
}