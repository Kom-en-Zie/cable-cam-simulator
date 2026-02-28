import * as CoordinatesConstants from '../constants/coordinates-constants.js';

export function pointToCanvasPoint(point, aPos) {
    const canvasWidth = window.innerWidth - CoordinatesConstants.padding * 2;

    const ratio = canvasWidth / aPos.x
    console.info('ratio', ratio)
    const oPos = {
        x: CoordinatesConstants.padding,
        y: CoordinatesConstants.padding + Math.max(aPos.y, 0) * ratio,
    };

    return {
        x: oPos.x + point.x * ratio,
        y: oPos.y + point.y * ratio,
    };
}
