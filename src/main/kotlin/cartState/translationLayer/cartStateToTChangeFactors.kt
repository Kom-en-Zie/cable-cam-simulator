package nl.komenzie.cableCam.cartState.translationLayer

import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.position.calculateT1T2
import kotlin.math.abs

fun CartState.toTChangeFactors(targetPos: Point, aPos: Point): TChangeFactors {
    val oPos = Point(0.0, 0.0)

    val (currentT1, currentT2) = Point(
        x = Line(oPos, position).length,
        y = Line(aPos, position).length,
    ).calculateT1T2()
    val (targetT1, targetT2) = Point(
        x = Line(oPos, targetPos).length,
        y = Line(aPos, targetPos).length,
    ).calculateT1T2()

    val diffT1 = abs(targetT1 - currentT1)
    val diffT2 = abs(targetT2 - currentT2)
    val total = diffT1 + diffT2
    if (total == 0.0) return TChangeFactors(0.5, 0.5)

    return TChangeFactors(
        changeT1Factor = diffT1 / total,
        changeT2Factor = diffT2 / total,
    )
}
