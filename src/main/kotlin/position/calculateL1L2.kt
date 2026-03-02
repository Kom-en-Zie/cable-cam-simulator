package nl.komenzie.cableCam.position

import nl.komenzie.cableCam.CableCamState
import kotlin.math.abs

fun CableCamState.calculateL1(t1: Double? = null, t2: Double? = null): Double {
    val t1Local = t1 ?: this.t1
    val t2Local = t2 ?: this.t2
    return abs((t2Local - t1Local) / 2)
}

fun CableCamState.calculateL2(t1: Double? = null, t2: Double? = null): Double {
    val t1Local = t1 ?: this.t1
    val t2Local = t2 ?: this.t2
    return abs((3 * t1Local - t2Local) / 4)
}
