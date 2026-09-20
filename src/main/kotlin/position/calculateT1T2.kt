package nl.komenzie.cableCam.position

import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point

private val oPos = Point(0.0, 0.0)

// Inverse of calculateL1/calculateL2's `abs((t2-t1)/2)` / `abs((3*t1-t2)/4)`: solving
// t2-t1 = 2*L1 and 3*t1-t2 = 4*L2 for t1/t2. L1/L2 are non-negative distances by
// construction, so both differences come out non-negative too and always round-trip
// cleanly through those abs() calls (see calculateCPos, which is the forward direction).
fun Point.calculateT1T2(aPos: Point): Pair<Double, Double> {
    val l1 = Line(oPos, this).length
    val l2 = Line(aPos, this).length
    val t1 = l1 + 2 * l2
    val t2 = 3 * l1 + 2 * l2
    return t1 to t2
}
