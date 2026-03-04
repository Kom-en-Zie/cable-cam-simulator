package nl.komenzie.cableCam.cartState

import nl.komenzie.cableCam.geometry.Point
import nl.komenzie.cableCam.position.movement.Movement

data class CartState(
    val position: Point,
    val movement: Movement,
)
