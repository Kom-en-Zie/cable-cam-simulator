package nl.komenzie.cableCam.position

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.exception.InvalidCableCamStateException
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Calculates cPos with aPos, lengthL1, and lengthL2 and assuming oPos is always (0, 0)
 *
 * It achieves this by calculating the intersection point between the circles
 * oPos with radius lengthL1
 * and aPos with radius lengthL2
 * that has the lowest y coordinate.
 */
fun CableCamState.calculateCPos(t1: Double? = null, t2: Double? = null): Point {
    val t1Local = t1 ?: this.t1
    val t2Local = t2 ?: this.t2
    val l1Local = if (t1 != null || t2 != null) this.calculateL1(t1Local, t2Local) else this.lengthL1
    val l2Local = if (t1 != null || t2 != null) this.calculateL2(t1Local, t2Local) else this.lengthL2

    /** [d] Distance between oPos and aPos */
    val d: Double = Line(oPos, aPos).length

    if (d > (t1Local + t2Local) / 4.0) throw InvalidCableCamStateException("The ropes are shorter than the minimum length required for oPos and aPos")
    if (d > l1Local + l2Local) throw InvalidCableCamStateException(
        "The circles of possibilities do not intersect." +
                " IMPORTANT: This is a math or code error, since the 'd > (t1 + t2) / 4.0' check is already passed"
    )
    if (d < abs(l1Local - l2Local)) throw InvalidCableCamStateException(
        "The circles of possibilities do not intersect. (one circle is within the other one)"
    )

    /** [a] The distance from the first center to the projection of the intersection points on the line connecting the centers */
    val a: Double = (l1Local.pow(2) - l2Local.pow(2) + d.pow(2)) / (2 * d)

    /** [h] The distance from that line to the intersection points */
    val h: Double = sqrt(l1Local.pow(2) - a.pow(2))

    // 1. Find the point P2 (the projection point on the line between centers)
    val x2 = oPos.x + a * (aPos.x - oPos.x) / d
    val y2 = oPos.y + a * (aPos.y - oPos.y) / d

    // 2. Determine the two possible intersection points using the perpendicular vector
    // The vector (aPos.x - oPos.x, aPos.y - oPos.y) is the line direction.
    // The perpendicular vector is (-(aPos.y - oPos.y), (aPos.x - oPos.x))

    val rx = -(aPos.y - oPos.y) * (h / d)
    val ry = (aPos.x - oPos.x) * (h / d)

    // Two possible points
    val p1 = Point(x2 + rx, y2 + ry)
    val p2 = Point(x2 - rx, y2 - ry)
    val p = if (p1.y < p2.y) p1 else p2

    return p

}
