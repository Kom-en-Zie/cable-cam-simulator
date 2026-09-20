package nl.komenzie.cableCam

import kotlin.test.Test
import kotlin.test.assertEquals
import nl.komenzie.cableCam.cartState.CartConfig
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorProperties
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.position.calculateT1T2
import nl.komenzie.cableCam.time.TimeState

/**
 * Regression coverage for calculateT1T2 being the true inverse of calculateCPos.
 * It previously ignored `aPos` entirely (`t1=x+2y`, `t2=3x+2y`), which happened to
 * only agree with the real (aPos-dependent) circle-intersection physics along a
 * coincidental line — everywhere else it pointed motors the wrong way.
 */
class CalculateT1T2Test {
    private val aPos = Point(40.0, 5.0)

    private fun freshState(t1: Double, t2: Double) = CableCamState(
        aPos, .75, .40, 3.5, t1, t2,
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        CartConfig(maxSpeed = 25.0, maxAcceleration = 5.0),
        TimeState(),
    )

    @Test
    fun roundTripsThroughCalculateCPosFromMainKtStartingValues() {
        val state = freshState(75.0, 110.0)

        val (t1, t2) = state.cPos.calculateT1T2(aPos)

        assertEquals(state.t1, t1, 1e-9)
        assertEquals(state.t2, t2, 1e-9)
    }

    @Test
    fun roundTripsForOtherReachablePositions() {
        val cases = listOf(70.0 to 100.0, 90.0 to 120.0, 80.0 to 150.0)

        for ((t1In, t2In) in cases) {
            val state = freshState(t1In, t2In)
            val cPos = state.cPos

            val (t1Out, t2Out) = cPos.calculateT1T2(aPos)

            assertEquals(t1In, t1Out, 1e-6, "t1 round-trip failed for cPos=$cPos")
            assertEquals(t2In, t2Out, 1e-6, "t2 round-trip failed for cPos=$cPos")
        }
    }
}
