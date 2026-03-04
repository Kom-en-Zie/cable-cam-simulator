package nl.komenzie.cableCam

import kotlinx.serialization.Serializable
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.MovementVector
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.time.TimeState

@Serializable
data class CableCamStateImmutable(
    val aPos: Point,
    val cHeight: Double,
    val cWidth: Double,
    val carWeight: Double,
    var t1: Double,
    var t2: Double,
    val motor1State: MotorState,
    val motor2State: MotorState,
    val timeState: TimeState,
    val oPos: Point,
    val w: Double,
    val lengthL1: Double,
    val lengthL2: Double,
    val cPos: Point,
    val l1: Line,
    val l2: Line,
    val movementVector: MovementVector,
)