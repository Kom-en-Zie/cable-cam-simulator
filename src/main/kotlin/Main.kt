package nl.komenzie.cableCam

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import nl.komenzie.cableCam.cartState.CartConfig
import nl.komenzie.cableCam.exception.InvalidCableCamStateException
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.parts.motors.MotorProperties
import nl.komenzie.cableCam.parts.motors.MotorState
import nl.komenzie.cableCam.position.movement.LinearLineMovement
import nl.komenzie.cableCam.time.TimeState
import java.lang.Thread.sleep
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

fun main() {
    var latestStateJson = ""
    var webClientConnected = false

    embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        routing {
            staticResources("/", "web") {
                default("index.html")
            }

            webSocket("/data") {
                println("Web client has connected!")
                webClientConnected = true
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
        75.0,
        110.0,
        MotorState(motorProperties, 0.0),
        MotorState(motorProperties, 0.0),
        CartConfig(
            maxSpeed = 25.0,
            acceleration = 5.0,
        ),
        timeState,
    )

    // Wait for the first client so timePassed doesn't advance into thin air.
    println("Waiting for a web client to connect...")
    while (!webClientConnected) sleep(100)

    // Run the simulation autonomously on a daemon thread so timePassed (and
    // therefore the desired state from the movement queue) advances in real
    // time. The main thread's readln() would otherwise block ticks indefinitely.
    Thread {
        while (true) {
            try {
                latestStateJson = cableCamState.toJson()
            } catch (e: InvalidCableCamStateException) {
                println("ERROR: " + e.message)
            }

            sleep(realTimeIncrementJavaDuration)
            cableCamState.update(calculationIncrements)
        }
    }.apply {
        isDaemon = true
        name = "cable-cam-sim"
    }.start()

    // Input loop: each "x;y" line enqueues a LinearLineMovement from the end
    // of the previously-queued movement to (x, y). Chaining from the last
    // enqueued endpoint (rather than from cableCamState.cPos) means new
    // targets pick up where the desired-state trajectory left off — the
    // actual carriage doesn't move on its own yet, so cPos would otherwise
    // make every new movement restart from the same spot.
    var lastQueuedEnd: Point = cableCamState.cPos
    var lastQueuedEndTime: Duration = cableCamState.timeState.timePassed

    while (true) {
        print("new target coordinates (x;y): ")
        val manualInput = readln()
        try {
            val (x, y) = manualInput.split(";").map { it.toDouble() }
            val newStartTime = maxOf(lastQueuedEndTime, cableCamState.timeState.timePassed)
            val movement = LinearLineMovement(
                cPosStart = lastQueuedEnd,
                cPosEnd = Point(x, y),
                startTime = newStartTime,
                speed = cableCamState.cartConfig.maxSpeed,
                acceleration = cableCamState.cartConfig.acceleration,
            )
            cableCamState.movementQueue.add(movement)
            lastQueuedEnd = movement.cPosEnd
            lastQueuedEndTime = movement.endTime
        } catch (_: NumberFormatException) {
            println("invalid input format, please enter x;y")
        }
    }
}
