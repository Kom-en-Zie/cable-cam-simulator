package nl.komenzie.cableCam.cartState.translationLayer

import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.TranslationVector
import nl.komenzie.cableCam.position.calculateT1T2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

fun CartState.toTChangeFactors(targetPos: Point, aPos: Point): TChangeFactors {
    val (currentT1, currentT2) = position.calculateT1T2(aPos)
    val (targetT1, targetT2) = targetPos.calculateT1T2(aPos)

    val diffT1 = targetT1 - currentT1
    val diffT2 = targetT2 - currentT2
    val total = abs(diffT1) + abs(diffT2)
    if (total == 0.0) return TChangeFactors(0.0, 0.0, 0.0)

    return TChangeFactors(
        changeT1Factor = diffT1 / total,
        changeT2Factor = diffT2 / total,
        totalChangeMagnitude = total,
    )
}

fun CartState.toTChangeFactors(translationVector: TranslationVector, aPos: Point): TChangeFactors {
    val targetPos = Point(
        x = position.x + cos(translationVector.angle.radians),
        y = position.y + sin(translationVector.angle.radians),
    )
    return toTChangeFactors(targetPos, aPos)
}
