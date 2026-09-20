package nl.komenzie.cableCam.cartState.translationLayer

/**
 * @param changeT1Factor Motor 1's signed share of the change, normalized so `abs(changeT1Factor) + abs(changeT2Factor) == 1`.
 * @param changeT2Factor Motor 2's signed share of the change, normalized the same way.
 * @param totalChangeMagnitude What `changeT1Factor`/`changeT2Factor` were normalized by — i.e. `changeT1Factor * totalChangeMagnitude` recovers the raw, unnormalized t1 change (and likewise for t2) for the direction this was computed from. Needed to scale back up to an absolute quantity (e.g. an acceleration) rather than just a direction split.
 */
data class TChangeFactors(
    val changeT1Factor: Double,
    val changeT2Factor: Double,
    val totalChangeMagnitude: Double,
)
