import type { CanvasPoint, Point } from '../types.js';

const PADDING_PX = 16;

/**
 * Maps between simulation-world coordinates (meters, Y up) and canvas
 * pixel coordinates (Y down). The viewport is sized so the support span
 * (oPos→aPos) fits horizontally inside the canvas with a fixed padding.
 *
 * Construct a new {@link Viewport} per frame from the latest state — it's
 * cheap and keeps the transform free of mutable state.
 */
export class Viewport {
    private readonly ratio: number;
    private readonly originX: number;
    private readonly originY: number;

    constructor(canvasSize: { width: number; height: number }, aPos: Point) {
        const usableWidth = canvasSize.width - PADDING_PX * 2;
        this.ratio = usableWidth / aPos.x;
        this.originX = PADDING_PX;
        this.originY = PADDING_PX + Math.max(aPos.y, 0) * this.ratio;
    }

    worldToScreen(point: Point): CanvasPoint {
        return {
            x: this.originX + point.x * this.ratio,
            y: this.originY - point.y * this.ratio,
        };
    }
}
