import type { Point } from '../../types.js';
import { Viewport } from '../viewport.js';

export interface LabelStyle {
    readonly color: string;
    /** CSS font shorthand, e.g. `"12px Arial"`. */
    readonly font: string;
    /** Pixel offset from the anchor — use to nudge labels above/beside their target. */
    readonly offset?: { readonly dx: number; readonly dy: number };
}

/** Text anchored at a world-space point with an optional pixel offset. */
export function drawWorldLabel(
    ctx: CanvasRenderingContext2D,
    anchor: Point,
    text: string,
    style: LabelStyle,
): void {
    const pos = Viewport.instance.worldToScreen(anchor);
    ctx.fillStyle = style.color;
    ctx.font = style.font;
    ctx.fillText(
        text,
        pos.x + (style.offset?.dx ?? 0),
        pos.y + (style.offset?.dy ?? 0),
    );
}
