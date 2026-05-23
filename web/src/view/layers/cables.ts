import type { CableCamState, Point } from '../../types.js';
import type { Layer } from '../renderer.js';
import { drawWorldDot } from '../util/draw-dot.js';
import { drawWorldLine } from '../util/draw-line.js';

const LEFT_CABLE_COLOR = '#3498db';
const RIGHT_CABLE_COLOR = '#e74c3c';
const CABLE_WIDTH_PX = 3;
const ANCHOR_RADIUS_PX = 4;
const ANCHOR_COLOR = 'white';

/** The two cables running from each anchor pole to the carriage. */
export class CablesLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState): void {
        this.drawCable(ctx, state.oPos, state.cPos, LEFT_CABLE_COLOR);
        this.drawCable(ctx, state.aPos, state.cPos, RIGHT_CABLE_COLOR);
    }

    private drawCable(
        ctx: CanvasRenderingContext2D,
        anchor: Point,
        carriage: Point,
        color: string,
    ): void {
        drawWorldLine(ctx, anchor, carriage, { color, width: CABLE_WIDTH_PX });
        drawWorldDot(ctx, anchor, { color: ANCHOR_COLOR, radius: ANCHOR_RADIUS_PX });
    }
}
