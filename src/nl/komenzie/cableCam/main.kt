package nl.komenzie.cableCam

import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorProperties
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.time.TimeState
import java.lang.Thread.sleep
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

fun main() {
    val simulationSpeed = 1.0
    val calculationIncrements = 10.toDuration(DurationUnit.MILLISECONDS)
    val realTimeIncrementJavaDuration = (calculationIncrements * simulationSpeed).toJavaDuration()

    val timeState = TimeState()

    val motorProperties = MotorProperties(
        1500.0,
        3.5,
    )

    val cableCamState = CableCamState(
        Point(40.0, 5.0),
        .75,
        .40,
        3.5,
        // TODO: come up with good starting values for t1 & t2
        110.0,
        35.0,
        MotorState(
            motorProperties,
            0.0,
        ),
        MotorState(
            motorProperties,
            0.0,
        ),
        timeState,
    )

    while (true) {
        sleep(realTimeIncrementJavaDuration)

        cableCamState.update(calculationIncrements)
    }
}
