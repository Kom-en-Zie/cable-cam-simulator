package nl.komenzie.cableCam

import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.time.TimeState

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
  val cPos: Point = TODO("Calculate the cable car position from t1, and t2")
  var l1: Line = Line(oPos, cPos)
  var l2: Line = Line(aPos, cPos)
}
