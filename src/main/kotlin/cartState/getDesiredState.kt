package nl.komenzie.cableCam.cartState

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.position.movement.queue.getCurrentMovement

fun CableCamState.getDesiredState(): CartState? {
    val movement = this.movementQueue.getCurrentMovement()
        ?: return this.movementQueue.lastReached

    return movement.calculatedDesiredCartState(this.timeState.timePassed)
}
