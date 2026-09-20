package nl.komenzie.cableCam.cartState.translationLayer

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.movementVector.targetVector.calculateTargetAccelerationVector

/**
 * Splits the cart's target acceleration across the two motors (via [toTChangeFactors]),
 * clamped to what each motor's [nl.komenzie.cableCam.parts.motors.MotorProperties.maxAcceleration] can actually deliver.
 * @return motor1Acceleration to motor2Acceleration, in m/s^2 of cable speed
 */
fun CableCamState.calculateMotorAccelerations(): Pair<Double, Double> {
    val targetAcceleration = calculateTargetAccelerationVector()
    val changeFactors = currentCartState.toTChangeFactors(targetAcceleration, aPos)

    val motor1Acceleration = (changeFactors.changeT1Factor * changeFactors.totalChangeMagnitude * targetAcceleration.acceleration)
        .coerceIn(-motor1State.properties.maxAcceleration, motor1State.properties.maxAcceleration)
    val motor2Acceleration = (changeFactors.changeT2Factor * changeFactors.totalChangeMagnitude * targetAcceleration.acceleration)
        .coerceIn(-motor2State.properties.maxAcceleration, motor2State.properties.maxAcceleration)

    return motor1Acceleration to motor2Acceleration
}
