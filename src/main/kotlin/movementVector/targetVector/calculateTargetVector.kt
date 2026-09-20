package nl.komenzie.cableCam.movementVector.targetVector

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.cartState.getDesiredState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.movementVector.MovementVector
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun CableCamState.calculateTargetVector(): MovementVector {
    val desiredState = this.getDesiredState() ?: this.currentCartState

    // Get distance between actual and desired state
    val distance = Line(this.cPos, desiredState.position).length

    // Extend length of desired vector (copy) by the distance
    val extendedDesiredVector = MovementVector(
        desiredState.movementVector.angle,
        desiredState.movementVector.speed + distance,
    )

    // Get the point at the end of the new desired vector
    val targetPoint = extendedDesiredVector.newPos(desiredState.position, 1.toDuration(DurationUnit.SECONDS))

    // Create a vector that runs from actual to the calculated point (and return it)
    val lineActualToTarget = Line(this.currentCartState.position, targetPoint)
    val angleActualToTarget = lineActualToTarget.angle
    val distanceActualToTarget = lineActualToTarget.length
    return MovementVector(
        angleActualToTarget,
        distanceActualToTarget,
    )
}
