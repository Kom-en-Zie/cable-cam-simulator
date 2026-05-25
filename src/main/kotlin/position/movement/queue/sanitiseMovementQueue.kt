package nl.komenzie.cableCam.position.movement.queue

import kotlin.time.Duration

fun MovementQueue.sanitiseMovementQueue() {
    val movementArray = this.toArray()
    var currentEndTime: Duration? = null
    movementArray.forEach { m ->
        if (this.cableCamState.timeState.timePassed >= m.endTime) {
            return@forEach this.retire(m)
        }
        if (currentEndTime != null && m.startTime < currentEndTime) {
            // Overlapping movement never played — drop without remembering.
            return@forEach this.removeElement(m)
        }
        // TODO: check if the distance between the end and startpoint can be reached within time
        currentEndTime = m.endTime
    }
}
