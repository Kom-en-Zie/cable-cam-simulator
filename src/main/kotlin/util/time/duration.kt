package nl.komenzie.cableCam.util.time

import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.toSeconds(): Double {
  return this.toDouble(DurationUnit.SECONDS)
}
