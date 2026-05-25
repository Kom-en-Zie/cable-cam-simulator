package nl.komenzie.cableCam.position

import nl.komenzie.cableCam.geometry.Point

fun Point.calculateT1T2(): Pair<Double, Double> {
    val t1 = x + 2 * y
    val t2 = 3 * x + 2 * y
    return t1 to t2
}
