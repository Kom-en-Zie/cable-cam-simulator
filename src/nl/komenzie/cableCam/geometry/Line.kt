package nl.komenzie.cableCam.geometry

import kotlin.math.pow
import kotlin.math.sqrt

data class Line(val p1: Point, val p2: Point) {
    val length: Double get() = sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
}
