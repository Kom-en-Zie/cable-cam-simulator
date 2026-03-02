package nl.komenzie.cableCam.geometry

import kotlinx.serialization.Serializable
import java.lang.Math.toDegrees

@Serializable
data class Angle(val radians: Double) {
    val degrees: Double = toDegrees(radians)
}
