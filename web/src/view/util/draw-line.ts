import type { Coordinate } from './coordinate.js';

export interface LineStyle {
    readonly color: string;
    readonly width?: number;
    /** Dash pattern in pixels — omit for a solid line. */
    readonly dash?: readonly number[];
}

/**
 * Draws a single stroked line segment between two canvas-space points.
 *
 * All three relevant stroke properties (color, width, dash) are set
 * every call, so prior `setLineDash`/`strokeStyle` state from other
 * drawing code does not leak in.
 */
export function drawLine(
    ctx: CanvasRenderingContext2D,
    from: Coordinate,
    to: Coordinate,
    style: LineStyle,
): void {
    ctx.strokeStyle = style.color;
    ctx.lineWidth = style.width ?? 1;
    ctx.setLineDash(style.dash !== undefined ? [...style.dash] : []);

    ctx.beginPath();
    ctx.moveTo(from.x, from.y);
    ctx.lineTo(to.x, to.y);
    ctx.stroke();
}
