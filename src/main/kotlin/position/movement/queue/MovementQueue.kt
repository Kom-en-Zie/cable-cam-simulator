package nl.komenzie.cableCam.position.movement.queue

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.position.movement.Movement

class MovementQueue(
    val cableCamState: CableCamState
) {
    private val queue = ArrayDeque<Movement>()

    /**
     * End-state of the most recently completed movement. Retained after the
     * queue empties so `getDesiredState` can keep returning that resting
     * target — otherwise the front-end's desired-state visualization would
     * vanish the moment a movement finished.
     */
    var lastReached: CartState? = null
        private set

    fun toArray(): Array<Movement> {
        return queue.toTypedArray()
    }

    fun removeElement(element: Movement) {
        queue.remove(element)
    }

    fun getFirst(): Movement? {
        return queue.firstOrNull()
    }

    fun add(movement: Movement) {
        queue.addLast(movement)
    }

    /** Mark a movement as completed: snapshot its end-state and drop it from the queue. */
    fun retire(movement: Movement) {
        lastReached = movement.calculatedDesiredCartState(movement.endTime)
        queue.remove(movement)
    }
}
