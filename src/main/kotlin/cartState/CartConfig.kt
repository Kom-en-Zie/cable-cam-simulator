package nl.komenzie.cableCam.cartState

import kotlinx.serialization.Serializable

@Serializable
data class CartConfig(
    val maxSpeed: Double = 10.0,
    val acceleration: Double = 2.0,
)
