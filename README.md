# Cable Cam Simulator

A 2D simulation of a pulley-driven cable cam — software-first, hardware-later. Type coordinates in your terminal, watch the carriage glide there in your browser.

```
       oPos                                              aPos
        ●━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━●
         \                                              /
          \                                            /
           \                                          /
            \                                        /
             \                                      /
              \                       ┌────┐       /
               \                      │    │      /
                \                     └────┘     /
                 \                       ↑      /
                                      carriage
```

Two motors. Two cables. One cart that should land exactly where you tell it to.

## Quick start

```bash
./gradlew run
```

Open <http://localhost:8080>. In the same terminal that ran Gradle:

```
new target coordinates (x;y): 20;3
```

The desired-state ghost zips to `(20, 3)` on the canvas. Queue more targets while it's still moving — they chain onto the end.

## Why this exists

A physical cable cam is being built. Before we strap motors to poles, we want to know exactly what those motors should do. This sandbox is where the math, the path planner, and the control logic get figured out.

## What's under the hood

**The pulley math is non-obvious.** Each motor doesn't just spool the visible cable — pulleys multiply rope length. The forward relationship comes straight from the mechanical layout:

```
L1 = |(t2 − t1) / 2|        (oPos → cart, visible diagonal)
L2 = |(3·t1 − t2) / 4|      (aPos → cart, visible diagonal)
```

`L1` / `L2` are the diagonals you see on the canvas; `t1` / `t2` are what the motors actually wind. The inverse drives the controller.

**Real-time, but split-brain.** The Kotlin simulation thread ticks every 10 ms. The WebSocket streams state at ~60 fps. The browser doesn't render on message arrival — it renders on `requestAnimationFrame`, so resizing and interacting never wait on the network.

**Two languages, one process.** Kotlin on the JVM runs the physics; TypeScript renders into a `<canvas>`. They share a Ktor WebSocket. Gradle compiles the TS bundle as part of `processResources`, so a single `./gradlew run` does everything.

## Architecture at a glance

```
┌── JVM process (Main.kt) ───────────────────────────────┐
│                                                        │
│   sim thread (10 ms) ──► state JSON ──► WebSocket /data│
│   stdin (x;y)        ──► enqueue Movement              │
│                                                        │
└────────────────────────────────────────────────────────┘
                              │
                              ▼
                    browser canvas (TypeScript)
                    rendered via requestAnimationFrame
```

## Layout

- `src/main/kotlin/` — simulation & Ktor server
  - `position/` — geometry (L1/L2 ↔ t1/t2, cPos intersection)
  - `cartState/` — derived state, target → motor translation layer
  - `position/movement/` — `Movement` types + `MovementQueue`
  - `parts/motors/` — motor properties and state
- `web/src/` — TypeScript frontend (compiles into `src/main/resources/web/`)
- `src/test/kotlin/` — JUnit tests (`./gradlew test`)

## Status

The path engine and the rendering pipeline work. The closed-loop controller — the piece that ties the two together so the *actual* cart (not just its ghost) tracks the desired path — is the next thing to build.
