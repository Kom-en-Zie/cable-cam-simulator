package nl.komenzie.cableCam.position.movement.queue

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.position.movement.Movement

class MovementQueue(
    val cableCamState: CableCamState
) {
    private val queue = ArrayDeque<Movement>()

    fun toArray(): Array<Movement> {
        return queue.toTypedArray()
    }

    fun removeElement(element: Movement) {
        queue.remove(element)
    }
}
