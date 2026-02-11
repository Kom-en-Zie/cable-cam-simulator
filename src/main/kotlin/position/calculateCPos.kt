package nl.komenzie.cableCam.position

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.time.TimeState
import kotlin.math.sqrt

fun CableCamState.calculateCPos(): Point {
    // You can use all variables inside CableCamState!
    // Just use this.[variableName], you can use this as every standard variable
    // The point of this function is to give a (new) value to this.cPos


    val a = 1 + (aPos.x / aPos.y)**2
    val b = 2 * (aPos.x / aPos.y) * ((L2**2 - L1**2 - aPos.x**2 - aPos.y**2) / 2 * aPos.y)
    val c = ((L2**2 - L1**2 - aPos.x**2 - aPos.y**2) / 2 * aPos.y)**2 - L1**2

    val x = CableCamState.L1L2IntersectionSolve(a, b, c)
    val y = sqrt(L1**2-x**2)

    return Point(x, y)




    // Example:
    val tempVariableThatYouDefinitelyShouldNotUse = this.cHeight * 8

    return Point(0.0, 0.0)  // TODO: to do

}
/*
fun CableCamState.L1L2IntersectionSolve(a: Double, b: Double, Ac: Double): Double{


    ABCsol1 = -b - sqrt(b**2 - 4*a*c) / 2 * a
    ABCsol2 = -b + sqrt(b**2 - 4*a*c) / 2 * a
    cPosX = min(ABCsol1, ABCsol2)


    Return Double(cPosX)
}