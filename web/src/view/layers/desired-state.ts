import type { CableCamState } from '../../types.js';
import type { Layer } from '../renderer.js';
import { drawWorldBox } from '../util/draw-box.js';
import { drawWorldLine } from '../util/draw-line.js';

const DESIRED_COLOR = 'dodgerblue';
const SQUARE_SIZE_PX = 14;
const VECTOR_WIDTH_PX = 2;

/**
 * Draws the target the driver should match: a blue square at the
 * desired position plus a blue movement vector showing intended
 * velocity (length = m/s at world scale, same convention as
 * MovementVectorLayer).
 *
 * Drawn at the bottom of the stack so the actual carriage/cables/etc.
 * remain visually dominant when the cam is on target.
 *
 * No-op when desiredState is null (no movement queued).
 */
export class DesiredStateLayer implements Layer {
    draw(ctx: CanvasRenderingContext2D, state: CableCamState): void {
        const desired = state.desiredState;
        if (desired === null) return;

        drawWorldBox(ctx, desired.position, {
            color: DESIRED_COLOR,
            width: SQUARE_SIZE_PX,
            height: SQUARE_SIZE_PX,
        });

        const { speed, angle } = desired.movementVector;
        if (speed === 0) return;

        const tipWorld = {
            x: desired.position.x + speed * Math.cos(angle.radians),
            y: desired.position.y + speed * Math.sin(angle.radians),
        };

        drawWorldLine(ctx, desired.position, tipWorld, {
            color: DESIRED_COLOR,
            width: VECTOR_WIDTH_PX,
        });
    }
}
