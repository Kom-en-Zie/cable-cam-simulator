import type { Point } from '../../types.js';
import { Viewport } from '../viewport.js';

export interface DotStyle {
    readonly color: string;
    /** Radius in screen pixels (constant regardless of viewport zoom). */
    readonly radius: number;
}

/** Filled circle anchored at a world-space point, sized in pixels. */
export function drawWorldDot(
    ctx: CanvasRenderingContext2D,
    center: Point,
    style: DotStyle,
): void {
    const pos = Viewport.instance.worldToScreen(center);
    ctx.fillStyle = style.color;
    ctx.beginPath();
    ctx.arc(pos.x, pos.y, style.radius, 0, Math.PI * 2);
    ctx.fill();
}
