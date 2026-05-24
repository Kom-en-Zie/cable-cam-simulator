import type { Point } from '../../types.js';
import { Viewport } from '../viewport.js';

export interface BoxStyle {
    readonly color: string;
    /** Width in screen pixels. */
    readonly width: number;
    /** Height in screen pixels. */
    readonly height: number;
}

/** Filled rectangle centered on a world-space point, sized in pixels. */
export function drawWorldBox(
    ctx: CanvasRenderingContext2D,
    center: Point,
    style: BoxStyle,
): void {
    const pos = Viewport.instance.worldToScreen(center);
    ctx.fillStyle = style.color;
    ctx.fillRect(
        pos.x - style.width / 2,
        pos.y - style.height / 2,
        style.width,
        style.height,
    );
}
