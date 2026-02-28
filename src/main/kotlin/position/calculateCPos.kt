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
fun CableCamState.calculateCPos(): Point {

    /** [d] Distance between oPos and aPos */
    val d: Double = Line(oPos, aPos).length

    if (d > (t1 + t2) / 4.0) throw InvalidCableCamStateException("The ropes are shorter than the minimum length required for oPos and aPos")
    if (d > lengthL1 + lengthL2) throw InvalidCableCamStateException(
        "The circles of possibilities do not intersect." +
                " IMPORTANT: This is a math or code error, since the 'd > (t1 + t2) / 4.0' check is already passed"
    )
    if (d < abs(lengthL1 - lengthL2)) throw InvalidCableCamStateException(
        "The circles of possibilities do not intersect. (one circle is within the other one)"
    )

    /** [a] The distance from the first center to the projection of the intersection points on the line connecting the centers */
    val a: Double = (lengthL1.pow(2) - lengthL2.pow(2) + d.pow(2)) / (2 * d)

    /** [h] The distance from that line to the intersection points */
    val h: Double = sqrt(lengthL1.pow(2) - a.pow(2))

    val sgnX: Int = if (aPos.x > 0) 1 else -1

    val y: Double = (a * aPos.y) / d
    val x: Double = (a * aPos.x + sgnX * aPos.y * h) / d

    return Point(x, y)

}
