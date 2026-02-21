package nl.komenzie.cableCam.position

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
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

    /** [a] The distance from the first center to the projection of the intersection points on the line connecting the centers */
    val a: Double = (lengthL1.pow(2) - lengthL2.pow(2) + d.pow(2)) / (2 * d)

    /** [h] The distance from that line to the intersection points */
    val h: Double = sqrt(lengthL1.pow(2) - a.pow(2))

    val sgnX: Int = if (aPos.x > 0) 1 else -1

    val y: Double = (a * aPos.y) / d
    val x: Double = (a * aPos.x + sgnX * aPos.y * h) / d

    return Point(x, y)

}
