import type { CableCamState } from '../../types.js';
import type { Layer } from '../renderer.js';
import type { Viewport } from '../viewport.js';

const CARRIAGE_COLOR = '#f1c40f';
const CARRIAGE_HALF_WIDTH_PX = 10;
const CARRIAGE_HALF_HEIGHT_PX = 5;
const LABEL_OFFSET_X_PX = -20;
const LABEL_OFFSET_Y_PX = -15;

/** The yellow carriage box plus its coordinate label. */
export class CarriageLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState, viewport: Viewport): void {
        const pos = viewport.worldToScreen(state.cPos);
        const corner = pos.offset(-CARRIAGE_HALF_WIDTH_PX, -CARRIAGE_HALF_HEIGHT_PX);
        const labelPos = pos.offset(LABEL_OFFSET_X_PX, LABEL_OFFSET_Y_PX);

        ctx.fillStyle = CARRIAGE_COLOR;
        ctx.fillRect(corner.x, corner.y, CARRIAGE_HALF_WIDTH_PX * 2, CARRIAGE_HALF_HEIGHT_PX * 2);

        ctx.fillStyle = 'white';
        ctx.font = '12px Arial';
        ctx.fillText(
            `Cam (${state.cPos.x.toFixed(1)}, ${state.cPos.y.toFixed(1)})`,
            labelPos.x,
            labelPos.y,
        );
    }
}
