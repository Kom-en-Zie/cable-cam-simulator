package nl.komenzie.cableCam.geometry

import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
data class Line(val p1: Point, val p2: Point) {
    val length: Double = sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
}
