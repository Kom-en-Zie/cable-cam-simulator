import type { Point } from '../../types.js';
import { Viewport } from '../viewport.js';
import type { Coordinate } from './coordinate.js';

export interface LineStyle {
    readonly color: string;
    readonly width?: number;
    /** Dash pattern in pixels — omit for a solid line. */
    readonly dash?: readonly number[];
}

/**
 * Draws a single stroked line segment in canvas/screen space.
 *
 * Color, width, and dash are set on every call so prior context state
 * cannot leak between segments.
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

/**
 * Same as {@link drawLine}, but takes world-space endpoints and projects
 * them through the {@link Viewport} singleton — line length therefore
 * scales with the viewport.
 */
export function drawWorldLine(
    ctx: CanvasRenderingContext2D,
    from: Point,
    to: Point,
    style: LineStyle,
): void {
    const vp = Viewport.instance;
    drawLine(ctx, vp.worldToScreen(from), vp.worldToScreen(to), style);
}
