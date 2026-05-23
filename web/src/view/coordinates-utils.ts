import { padding } from './coordinates-constants.js';
import type { CanvasPoint, Point } from '../types.js';

export function pointToCanvasPoint(point: Point, aPos: Point): CanvasPoint {
    const canvasWidth = window.innerWidth - padding * 2;

    const ratio = canvasWidth / aPos.x;
    const oPos: CanvasPoint = {
        x: padding,
        y: padding + Math.max(aPos.y, 0) * ratio,
    };

    return {
        x: oPos.x + point.x * ratio,
        y: oPos.y - point.y * ratio,
    };
}
