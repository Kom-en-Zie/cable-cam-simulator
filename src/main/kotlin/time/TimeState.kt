package nl.komenzie.cableCam.time

import kotlin.time.Duration

class TimeState(
    var timePassed: Duration = Duration.ZERO,
) {
    fun update(deltaTime: Duration) {
        timePassed += deltaTime
    }
}
