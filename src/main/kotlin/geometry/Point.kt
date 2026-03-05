package nl.komenzie.cableCam.geometry

import kotlinx.serialization.Serializable

@Serializable
data class Point(val x: Double, val y: Double) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)
}
