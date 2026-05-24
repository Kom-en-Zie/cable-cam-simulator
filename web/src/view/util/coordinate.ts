/**
 * Immutable 2D point in canvas/screen pixel space.
 *
 * World-space points coming off the wire stay as the plain {@link Point}
 * JSON shape — `Coordinate` is the type produced by {@link Viewport} and
 * consumed by the drawing helpers in this folder.
 */
export class Coordinate {
    constructor(
        readonly x: number,
        readonly y: number,
    ) {}

    /** Returns a new Coordinate translated by (dx, dy). */
    offset(dx: number, dy: number): Coordinate {
        return new Coordinate(this.x + dx, this.y + dy);
    }
}
