package nl.komenzie.cableCam

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorProperties
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.time.TimeState
import java.lang.Thread.sleep
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

fun main() {
    // 1. Shared state that is thread-safe
    var latestStateJson = ""

    // 2. Start Ktor in a background thread
    val server = embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        routing {
            staticResources("/", "web") {
                default("index.html")
            }

            webSocket("/data") {
                while (true) {
                    // Send the current state to the browser every 16ms (~60fps)
                    send(latestStateJson)
                    delay(16)
                }
            }
        }
    }.start(wait = false)


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
        println("test printje")

        cableCamState.update(calculationIncrements)
        latestStateJson = cableCamState.toJson()
    }
}
