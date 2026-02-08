package nl.komenzie.cableCam.parts.motors

/**
 * @param speed The Current speed (m/s) of cable input/output. Pulling cable in is a negative number
 * @param properties The properties of this motor
 */
class MotorState(
    val properties: MotorProperties,
    var speed: Double,
) {
  //
}
