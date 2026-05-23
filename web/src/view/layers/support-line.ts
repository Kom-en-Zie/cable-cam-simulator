import type { CableCamState } from '../../types.js';
import type { Layer } from '../renderer.js';
import { drawLine } from '../util/draw-line.js';
import type { Viewport } from '../viewport.js';

/** Dashed line from the origin pole (oPos) to the far pole (aPos). */
export class SupportLineLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState, viewport: Viewport): void {
        drawLine(
            ctx,
            viewport.worldToScreen(state.oPos),
            viewport.worldToScreen(state.aPos),
            { color: '#555', dash: [5, 5] },
        );
    }
}
