package nl.komenzie.cableCam.time

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
class TimeState(
    var timePassed: Duration = Duration.ZERO,
) {
    fun update(deltaTime: Duration) {
        timePassed += deltaTime
    }
}
