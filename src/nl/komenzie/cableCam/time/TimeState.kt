package nl.komenzie.cableCam.time

import kotlin.time.Duration

class TimeState(
    val timePassed: Duration = Duration.ZERO,
) {
    fun update(deltaTime: Duration): TimeState {
        return TimeState(timePassed + deltaTime)
    }

}