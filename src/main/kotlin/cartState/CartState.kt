package nl.komenzie.cableCam.cartState

import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.movementVector.MovementVector

data class CartState(
    val position: Point,
    val movementVector: MovementVector,
)
