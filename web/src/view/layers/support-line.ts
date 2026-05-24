import type { CableCamState } from '../../types.js';
import type { Layer } from '../renderer.js';
import { drawWorldLine } from '../util/draw-line.js';

/** Dashed line from the origin pole (oPos) to the far pole (aPos). */
export class SupportLineLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState): void {
        drawWorldLine(ctx, state.oPos, state.aPos, { color: '#555', dash: [5, 5] });
    }
}
