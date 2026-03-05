package nl.komenzie.cableCam.control

enum class ControlMode {
    /** Inputs desired state (position and movement). The system will then try to match this state */
    STATE_INPUT,

    /** Inputs desired movement (e.g. joysticks) */
    MOVEMENT_INPUT,
}