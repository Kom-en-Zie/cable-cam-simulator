import type { Point } from '../types.js';
import { Coordinate } from './util/coordinate.js';

const PADDING_PX = 16;

/**
 * Maps between simulation-world coordinates (meters, Y up) and canvas
 * pixel coordinates (Y down). The viewport is sized so the support span
 * (oPos→aPos) fits horizontally inside the canvas with a fixed padding.
 *
 * Singleton: there is one Viewport per browser tab, which is safe — each
 * connected client runs its own JS runtime. Reconfigured per frame by
 * {@link Renderer} before any layer draws.
 */
export class Viewport {
    private static singleton: Viewport | null = null;

    private ratio: number = 0;
    private originX: number = PADDING_PX;
    private originY: number = PADDING_PX;

    private constructor() {}

    static get instance(): Viewport {
        if (Viewport.singleton === null) {
            Viewport.singleton = new Viewport();
        }
        return Viewport.singleton;
    }

    /** Recompute the transform from the latest canvas size and far-pole position. */
    configure(canvasSize: { width: number; height: number }, aPos: Point): void {
        const usableWidth = canvasSize.width - PADDING_PX * 2;
        this.ratio = usableWidth / aPos.x;
        this.originX = PADDING_PX;
        this.originY = PADDING_PX + Math.max(aPos.y, 0) * this.ratio;
    }

    worldToScreen(point: Point): Coordinate {
        return new Coordinate(
            this.originX + point.x * this.ratio,
            this.originY - point.y * this.ratio,
        );
    }
}
