package nl.komenzie.cableCam.position

import nl.komenzie.cableCam.CableCamState
import nl.komenzie.cableCam.geometry.Point

fun CableCamState.calculateCPos(): Point {
    // You can use all variables inside CableCamState!
    // Just use this.[variableName], you can use this as every standard variable
    // The point of this function is to give a (new) value to this.cPos

    // Example:
    val tempVariableThatYouDefinitelyShouldNotUse = this.cHeight * 8

    return Point(0.0, 0.0)  // TODO: implement
}
