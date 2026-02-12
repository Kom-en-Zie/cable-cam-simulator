package nl.komenzie.cableCam.position

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.time.TimeState
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

fun CableCamState.calculateCPos(): Point {
    // You can use all variables inside CableCamState!
    // Just use this.[variableName], you can use this as every standard variable
    // The point of this function is to give a (new) value to this.cPos


    val a = 1 + (aPos.x / aPos.y).pow(2)
    val b = 2 * (aPos.x / aPos.y) * ((l2.pow(2) - l1.pow(2) - aPos.x.pow(2) - aPos.y.pow(2)) / 2 * aPos.y)
    val c = ((l2.pow(2) - l1.pow(2) - aPos.x.pow(2) - aPos.y.pow(2)) / 2 * aPos.y).pow(2) - l1.pow(2)

    val x = this.l1l2IntersectionSolve(a, b, c)
    val y = sqrt(l1.pow(2) - x.pow(2))

    return Point(x, y)

}

fun CableCamState.l1l2IntersectionSolve(a: Double, b: Double, c: Double): Double {

    val ABCsol1 = -b - sqrt(b.pow(2) - 4 * a * c) / 2 * a
    val ABCsol2 = -b + sqrt(b.pow(2) - 4 * a * c) / 2 * a
    val cPosX = min(ABCsol1, ABCsol2)

    return cPosX
}