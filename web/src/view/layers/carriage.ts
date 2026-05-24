import type { CableCamState } from '../../types.js';
import type { Layer } from '../renderer.js';
import { drawWorldBox } from '../util/draw-box.js';
import { drawWorldLabel } from '../util/draw-label.js';

const CARRIAGE_COLOR = '#f1c40f';
const CARRIAGE_WIDTH_PX = 20;
const CARRIAGE_HEIGHT_PX = 10;
const LABEL_FONT = '12px Arial';
const LABEL_COLOR = 'white';
const LABEL_OFFSET_X_PX = -20;
const LABEL_OFFSET_Y_PX = -15;

/** The yellow carriage box plus its coordinate label. */
export class CarriageLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState): void {
        drawWorldBox(ctx, state.cPos, {
            color: CARRIAGE_COLOR,
            width: CARRIAGE_WIDTH_PX,
            height: CARRIAGE_HEIGHT_PX,
        });
        drawWorldLabel(
            ctx,
            state.cPos,
            `Cam (${state.cPos.x.toFixed(1)}, ${state.cPos.y.toFixed(1)})`,
            {
                color: LABEL_COLOR,
                font: LABEL_FONT,
                offset: { dx: LABEL_OFFSET_X_PX, dy: LABEL_OFFSET_Y_PX },
            },
        );
    }
}
