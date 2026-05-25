package nl.komenzie.cableCam.cartState

import kotlinx.serialization.Serializable
import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.MovementVector

@Serializable
data class CartState(
    val position: Point,
    val movementVector: MovementVector,
)
