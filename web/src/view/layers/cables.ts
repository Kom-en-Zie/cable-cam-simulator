import type { CableCamState, Point } from '../../types.js';
import type { Layer } from '../renderer.js';
import type { Viewport } from '../viewport.js';

const LEFT_CABLE_COLOR = '#3498db';
const RIGHT_CABLE_COLOR = '#e74c3c';
const ANCHOR_RADIUS_PX = 4;

/** The two cables running from each anchor pole to the carriage. */
export class CablesLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState, viewport: Viewport): void {
        this.drawCable(ctx, viewport, state.oPos, state.cPos, LEFT_CABLE_COLOR);
        this.drawCable(ctx, viewport, state.aPos, state.cPos, RIGHT_CABLE_COLOR);
    }

    private drawCable(
        ctx: CanvasRenderingContext2D,
        viewport: Viewport,
        anchor: Point,
        carriage: Point,
        color: string,
    ): void {
        const start = viewport.worldToScreen(anchor);
        const end = viewport.worldToScreen(carriage);

        ctx.strokeStyle = color;
        ctx.lineWidth = 3;
        ctx.beginPath();
        ctx.moveTo(start.x, start.y);
        ctx.lineTo(end.x, end.y);
        ctx.stroke();

        ctx.fillStyle = 'white';
        ctx.beginPath();
        ctx.arc(start.x, start.y, ANCHOR_RADIUS_PX, 0, Math.PI * 2);
        ctx.fill();
    }
}
