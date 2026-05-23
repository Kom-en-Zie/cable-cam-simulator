import type { CableCamState } from '../../types.js';
import type { Layer } from '../renderer.js';
import type { Viewport } from '../viewport.js';

/** Dashed line from the origin pole (oPos) to the far pole (aPos). */
export class SupportLineLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState, viewport: Viewport): void {
        const start = viewport.worldToScreen(state.oPos);
        const end = viewport.worldToScreen(state.aPos);

        ctx.strokeStyle = '#555';
        ctx.setLineDash([5, 5]);
        ctx.beginPath();
        ctx.moveTo(start.x, start.y);
        ctx.lineTo(end.x, end.y);
        ctx.stroke();
        ctx.setLineDash([]);
    }
}
