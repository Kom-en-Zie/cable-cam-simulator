package nl.komenzie.cableCam.position.movement.queue

import nl.komenzie.cableCam.position.movement.Movement

fun MovementQueue.getCurrentMovement(): Movement? {
    this.sanitiseMovementQueue()

    val movement = this.getFirst() ?: return null
    if (this.cableCamState.timeState.timePassed < movement.startTime) return null

    // We don't need to check if the movement is finished, because sanitiseMovementQueue would've removed it
    return movement
}
