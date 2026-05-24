import type { CableCamState } from '../types.js';
import { Viewport } from './viewport.js';

/**
 * One visual concern (background grid, cables, HUD, …) implements
 * {@link Layer}. The {@link Renderer} composes a fixed-order list of
 * them and walks it on every draw call.
 *
 * Layers don't take a `Viewport` parameter — the renderer reconfigures
 * the {@link Viewport.instance} singleton before invoking layers, and
 * the world-space drawing utilities read from it directly.
 */
export interface Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState): void;
}

export class Renderer {
    constructor(
        private readonly ctx: CanvasRenderingContext2D,
        private readonly canvas: HTMLCanvasElement,
        private readonly layers: readonly Layer[],
    ) {}

    draw(state: CableCamState): void {
        Viewport.instance.configure(
            { width: this.canvas.width, height: this.canvas.height },
            state.aPos,
        );
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        for (const layer of this.layers) {
            layer.draw(this.ctx, state);
        }
    }
}
