package nl.komenzie.cableCam

import kotlinx.serialization.json.Json
import nl.komenzie.cableCam.cartState.CartConfig
import nl.komenzie.cableCam.cartState.CartState
import nl.komenzie.cableCam.geometry.Line
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.MovementVector
import nl.komenzie.cableCam.movementVector.calculateMovementVector
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.position.calculateCPos
import nl.komenzie.cableCam.position.calculateL1
import nl.komenzie.cableCam.position.calculateL2
import nl.komenzie.cableCam.position.movement.queue.MovementQueue
import nl.komenzie.cableCam.time.TimeState
import kotlin.time.Duration

class CableCamState(
    val aPos: Point,
    val cHeight: Double,
    val cWidth: Double,
    val carWeight: Double,
    var t1: Double,
    var t2: Double,
    val motor1State: MotorState,
    val motor2State: MotorState,
    val cartConfig: CartConfig,
    val timeState: TimeState = TimeState(),
) {
    val oPos: Point = Point(0.0, 0.0)
    val w: Double get() = aPos.x
    val lengthL1: Double get() = this.calculateL1()
    val lengthL2: Double get() = this.calculateL2()
    val cPos: Point get() = this.calculateCPos()
    val l1: Line get() = Line(oPos, cPos)
    val l2: Line get() = Line(aPos, cPos)
    val movementVector: MovementVector get() = this.calculateMovementVector()

    // Properties that are not in the ImmutableState
    val currentCartState: CartState get() = CartState(cPos, movementVector)
    val movementQueue: MovementQueue = MovementQueue()

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
        val immutable = CableCamStateImmutable(
            aPos,
            cHeight,
            cWidth,
            carWeight,
            t1,
            t2,
            motor1State,
            motor2State,
            timeState,
            oPos,
            w,
            lengthL1,
            lengthL2,
            cPos,
            l1,
            l2,
            movementVector
        )
        return Json.encodeToString(immutable)
    }
}
