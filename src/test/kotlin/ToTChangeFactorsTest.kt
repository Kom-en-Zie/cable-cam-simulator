package nl.komenzie.cableCam

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.cartState.translationLayer.toTChangeFactors
import nl.komenzie.cableCam.geometry.Angle
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.MovementVector
import nl.komenzie.cableCam.position.calculateT1T2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Regression test for a bug where `calculateMotorAccelerations` multiplied the
 * *normalized* `changeT1Factor`/`changeT2Factor` (which sum to 1 by absolute value —
 * a pure direction split) directly by an absolute acceleration magnitude. That silently
 * scaled the applied acceleration by `1/totalChangeMagnitude` instead of matching the
 * intended magnitude — a factor that varies with direction and position, so different
 * moves ended up with wildly different effective control-loop gain. `totalChangeMagnitude`
 * exists to undo that normalization before scaling by an absolute quantity.
 */
class ToTChangeFactorsTest {
    private val aPos = Point(40.0, 5.0)
    private val position = Point(14.9464, -9.1025)

    @Test
    fun factorsStillSumToOneByAbsoluteValue() {
        val cartState = CartState(position, MovementVector(Angle(0.0), 0.0))
        val changeFactors = cartState.toTChangeFactors(MovementVector(Angle(Math.toRadians(140.33)), 1.0), aPos)

        assertEquals(1.0, abs(changeFactors.changeT1Factor) + abs(changeFactors.changeT2Factor), 1e-9)
    }

    @Test
    fun scalingByTotalChangeMagnitudeRecoversTheRawDirectionalDerivative() {
        val cartState = CartState(position, MovementVector(Angle(0.0), 0.0))
        val angle = Angle(Math.toRadians(140.33))

        val changeFactors = cartState.toTChangeFactors(MovementVector(angle, 1.0), aPos)

        // Independently obtained expectation: the raw (unnormalized) t1/t2 change for a
        // one-unit step in this direction, via the already-verified calculateT1T2.
        val unitStepTarget = Point(position.x + cos(angle.radians), position.y + sin(angle.radians))
        val (currentT1, currentT2) = position.calculateT1T2(aPos)
        val (targetT1, targetT2) = unitStepTarget.calculateT1T2(aPos)

        assertEquals(targetT1 - currentT1, changeFactors.changeT1Factor * changeFactors.totalChangeMagnitude, 1e-9)
        assertEquals(targetT2 - currentT2, changeFactors.changeT2Factor * changeFactors.totalChangeMagnitude, 1e-9)
    }

    @Test
    fun totalChangeMagnitudeIsNotTriviallyOne() {
        // Guards against a regression back to the bug going unnoticed: if this ever
        // degenerates to ~1.0 for a representative direction, the missing-multiplication
        // bug becomes invisible again because skipping it would happen to work by luck.
        val cartState = CartState(position, MovementVector(Angle(0.0), 0.0))
        val changeFactors = cartState.toTChangeFactors(MovementVector(Angle(Math.toRadians(140.33)), 1.0), aPos)

        assertTrue(
            abs(changeFactors.totalChangeMagnitude - 1.0) > 0.5,
            "expected totalChangeMagnitude to meaningfully differ from 1.0 here " +
                "(was ${changeFactors.totalChangeMagnitude}), otherwise this test can't catch the scaling bug",
        )
    }
}
