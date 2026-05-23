import { pointToCanvasPoint } from './rendering/coordinates-utils.js';
import type { CableCamState, CanvasPoint, Point } from './types.js';

function getCanvas(): HTMLCanvasElement {
    const el = document.getElementById('simCanvas');
    if (!(el instanceof HTMLCanvasElement)) {
        throw new Error('Expected #simCanvas to be a <canvas> element');
    }
    return el;
}

function get2DContext(canvas: HTMLCanvasElement): CanvasRenderingContext2D {
    const ctx = canvas.getContext('2d');
    if (ctx === null) {
        throw new Error('Failed to acquire a 2D rendering context');
    }
    return ctx;
}

function toCanvas(point: Point, aPos: Point): CanvasPoint {
    return pointToCanvasPoint(point, aPos);
}

function drawSupportLine(
    ctx: CanvasRenderingContext2D,
    p1: Point,
    p2: Point,
    aPos: Point,
): void {
    const start = toCanvas(p1, aPos);
    const end = toCanvas(p2, aPos);

    ctx.strokeStyle = '#555';
    ctx.setLineDash([5, 5]);
    ctx.beginPath();
    ctx.moveTo(start.x, start.y);
    ctx.lineTo(end.x, end.y);
    ctx.stroke();
    ctx.setLineDash([]);
}

function drawCable(
    ctx: CanvasRenderingContext2D,
    p1: Point,
    p2: Point,
    color: string,
    aPos: Point,
): void {
    const start = toCanvas(p1, aPos);
    const end = toCanvas(p2, aPos);

    ctx.strokeStyle = color;
    ctx.lineWidth = 3;
    ctx.beginPath();
    ctx.moveTo(start.x, start.y);
    ctx.lineTo(end.x, end.y);
    ctx.stroke();

    ctx.fillStyle = 'white';
    ctx.beginPath();
    ctx.arc(start.x, start.y, 4, 0, Math.PI * 2);
    ctx.fill();
}

function drawCarriage(ctx: CanvasRenderingContext2D, cPos: Point, aPos: Point): void {
    const pos = toCanvas(cPos, aPos);
    ctx.fillStyle = '#f1c40f';
    ctx.fillRect(pos.x - 10, pos.y - 5, 20, 10);

    ctx.fillStyle = 'white';
    ctx.font = '12px Arial';
    ctx.fillText(
        `Cam (${cPos.x.toFixed(1)}, ${cPos.y.toFixed(1)})`,
        pos.x - 20,
        pos.y - 15,
    );
}

function draw(
    ctx: CanvasRenderingContext2D,
    canvas: HTMLCanvasElement,
    state: CableCamState,
): void {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    drawSupportLine(ctx, state.oPos, state.aPos, state.aPos);
    drawCable(ctx, state.oPos, state.cPos, '#3498db', state.aPos);
    drawCable(ctx, state.aPos, state.cPos, '#e74c3c', state.aPos);
    drawCarriage(ctx, state.cPos, state.aPos);
}

const canvas = getCanvas();
const ctx = get2DContext(canvas);

canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

const socket = new WebSocket(`ws://${window.location.host}/data`);
socket.addEventListener('message', (event: MessageEvent<string>) => {
    const state = JSON.parse(event.data) as CableCamState;
    draw(ctx, canvas, state);
});
