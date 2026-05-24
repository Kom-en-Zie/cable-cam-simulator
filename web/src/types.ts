/**
 * Mirrors {@link nl.komenzie.cableCam.CableCamStateImmutable} on the Kotlin side.
 * Field names and types must stay in sync with the kotlinx-serialization JSON output.
 */

export interface Point {
    readonly x: number;
    readonly y: number;
}

export interface Angle {
    readonly radians: number;
    readonly degrees: number;
}

export interface Line {
    readonly p1: Point;
    readonly p2: Point;
    readonly length: number;
    readonly angle: Angle;
}

export interface MotorProperties {
    readonly maxPower: number;
    readonly maxAcceleration: number;
}

export interface MotorState {
    readonly properties: MotorProperties;
    readonly speed: number;
}

export interface MovementVector {
    readonly angle: Angle;
    readonly speed: number;
}

export interface TimeState {
    /** kotlinx-serialization emits {@link kotlin.time.Duration} as an ISO-8601 string (e.g. "PT1.234S"). */
    readonly timePassed: string;
}

export interface CableCamState {
    readonly aPos: Point;
    readonly cHeight: number;
    readonly cWidth: number;
    readonly carWeight: number;
    readonly t1: number;
    readonly t2: number;
    readonly motor1State: MotorState;
    readonly motor2State: MotorState;
    readonly timeState: TimeState;
    readonly oPos: Point;
    readonly w: number;
    readonly lengthL1: number;
    readonly lengthL2: number;
    readonly cPos: Point;
    readonly l1: Line;
    readonly l2: Line;
    readonly movementVector: MovementVector;
}
