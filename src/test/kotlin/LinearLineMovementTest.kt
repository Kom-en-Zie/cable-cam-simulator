package nl.komenzie.cableCam

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import nl.komenzie.cableCam.cartState.CartConfig
import nl.komenzie.cableCam.cartState.getDesiredState
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorProperties
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.position.movement.LinearLineMovement
import nl.komenzie.cableCam.time.TimeState
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class LinearLineMovementTest {
    private fun freshState() = CableCamState(
        Point(40.0, 5.0),
        .75, .40, 3.5,
        75.0, 110.0,
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        MotorState(MotorProperties(1500.0, 3.5), 0.0),
        CartConfig(maxSpeed = 25.0, acceleration = 5.0),
        TimeState(),
    )

    private fun linearMovement(
        from: Point,
        to: Point,
        startTime: Duration = Duration.ZERO,
    ) = LinearLineMovement(
        cPosStart = from,
        cPosEnd = to,
        startTime = startTime,
        speed = 25.0,
        acceleration = 5.0,
    )

    /** Regression: Movement.endTime used to capture totalTime before the subclass had set it. */
    @Test
    fun endTimeReflectsTotalTime() {
        val movement = linearMovement(Point(0.0, 0.0), Point(10.0, 0.0))
        assertNotEquals(Duration.ZERO, movement.endTime)
        assertEquals(movement.startTime + movement.totalTime, movement.endTime)
    }

    /** Regression: short tracks used to assume top speed was reachable, overshooting cPosEnd. */
    @Test
    fun shortTrackUsesTriangularProfile() {
        val track = 5.0
        val movement = linearMovement(Point(0.0, 0.0), Point(track, 0.0))
        val expectedPeak = sqrt(5.0 * track) // sqrt(a·L)
        assertEquals(expectedPeak, movement.peakSpeed, 1e-9)
        assertTrue(movement.peakSpeed < 25.0, "peakSpeed should be capped below the requested max")
    }

    @Test
    fun longTrackRetainsRequestedTopSpeed() {
        val movement = linearMovement(Point(0.0, 0.0), Point(200.0, 0.0))
        assertEquals(25.0, movement.peakSpeed, 1e-9)
    }

    /**
     * End-to-end: when a movement is queued, the desired state advances smoothly along the
     * track, reaches the midpoint at totalTime/2, and is sanitised away once endTime passes.
     */
    @Test
    fun desiredStateAdvancesAndExpires() {
        val state = freshState()
        val start = state.cPos
        val movement = linearMovement(start, Point(20.0, -12.0), state.timeState.timePassed)
        state.movementQueue.add(movement)

        state.update(movement.totalTime / 2)
        val mid = state.getDesiredState()!!.position
        val midDistance = sqrt((mid.x - start.x).let { it * it } + (mid.y - start.y).let { it * it })
        assertEquals(movement.trackLength / 2, midDistance, 1e-3)

        state.update(movement.totalTime / 2 + 10.toDuration(DurationUnit.MILLISECONDS))
        assertNull(state.getDesiredState())
    }
}
