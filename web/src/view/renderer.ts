import type { CableCamState } from '../types.js';
import { Viewport } from './viewport.js';

/**
 * One visual concern (background grid, cables, HUD, …) implements
 * {@link Layer}. The {@link Renderer} composes a fixed-order list of
 * them and walks it on every draw call.
 */
export interface Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState, viewport: Viewport): void;
}

export class Renderer {
    constructor(
        private readonly ctx: CanvasRenderingContext2D,
        private readonly canvas: HTMLCanvasElement,
        private readonly layers: readonly Layer[],
    ) {}

    draw(state: CableCamState): void {
        const viewport = new Viewport(
            { width: this.canvas.width, height: this.canvas.height },
            state.aPos,
        );
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        for (const layer of this.layers) {
            layer.draw(this.ctx, state, viewport);
        }
    }
}
