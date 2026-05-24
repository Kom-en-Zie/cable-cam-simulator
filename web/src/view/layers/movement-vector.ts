import type { CableCamState } from '../../types.js';
import type { Layer } from '../renderer.js';
import { drawWorldLine } from '../util/draw-line.js';

const VECTOR_COLOR = 'yellow';
const VECTOR_WIDTH_PX = 2;

/**
 * Draws the carriage's instantaneous movement vector as a thin yellow
 * line originating at cPos. The line covers exactly one second of
 * travel at the current speed, so its world-space length equals the
 * m/s value — same scale as everything else routed through the
 * {@link Viewport}.
 *
 * Skipped when speed is zero (the segment would degenerate to a point).
 */
export class MovementVectorLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState): void {
        const { speed, angle } = state.movementVector;
        if (speed === 0) return;

        const tipWorld = {
            x: state.cPos.x + speed * Math.cos(angle.radians),
            y: state.cPos.y + speed * Math.sin(angle.radians),
        };

        drawWorldLine(ctx, state.cPos, tipWorld, {
            color: VECTOR_COLOR,
            width: VECTOR_WIDTH_PX,
        });
    }
}
