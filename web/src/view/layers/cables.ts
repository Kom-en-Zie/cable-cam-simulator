import type { CableCamState, Point } from '../../types.js';
import type { Layer } from '../renderer.js';
import type { Coordinate } from '../util/coordinate.js';
import { drawLine } from '../util/draw-line.js';
import type { Viewport } from '../viewport.js';

const LEFT_CABLE_COLOR = '#3498db';
const RIGHT_CABLE_COLOR = '#e74c3c';
const CABLE_WIDTH_PX = 3;
const ANCHOR_RADIUS_PX = 4;
const ANCHOR_COLOR = 'white';

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

        drawLine(ctx, start, end, { color, width: CABLE_WIDTH_PX });
        this.drawAnchorDot(ctx, start);
    }

    private drawAnchorDot(ctx: CanvasRenderingContext2D, center: Coordinate): void {
        ctx.fillStyle = ANCHOR_COLOR;
        ctx.beginPath();
        ctx.arc(center.x, center.y, ANCHOR_RADIUS_PX, 0, Math.PI * 2);
        ctx.fill();
    }
}
