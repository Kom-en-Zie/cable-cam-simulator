package nl.komenzie.cableCam

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.position.calculateCPos
import nl.komenzie.cableCam.time.TimeState
import kotlin.time.Duration

@Serializable
class CableCamState(
    val aPos: Point,
    val cHeight: Double,
    val cWidth: Double,
    val carWeight: Double,
    var t1: Double,
    var t2: Double,
    val motor1State: MotorState,
    val motor2State: MotorState,
    val timeState: TimeState = TimeState(),
) {
    val oPos: Point = Point(0.0, 0.0)
    val w: Double get() = aPos.x
    val cPos: Point get() = this.calculateCPos()
    val l1: Line get() = Line(oPos, cPos)
    val l2: Line get() = Line(aPos, cPos)

    /**
     * @param deltaTime The time progression that needs to be processed
     * @return reference to itself
     */
    fun update(deltaTime: Duration): CableCamState {
        t1 += motor1State.getPassedCableLength(deltaTime)
        t2 += motor2State.getPassedCableLength(deltaTime)

        timeState.update(deltaTime)

        return this
    }

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}
