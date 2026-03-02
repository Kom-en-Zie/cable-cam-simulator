package nl.komenzie.cableCam.geometry

import kotlinx.serialization.Serializable
import java.lang.Math.toRadians

@Serializable
data class Angle(val degrees: Double) {
    val radians: Double = toRadians(degrees)
}
