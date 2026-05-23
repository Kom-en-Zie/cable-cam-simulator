import type { CableCamState, Point } from './types.js';
import { Viewport } from './view/viewport.js';

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

function drawSupportLine(
    ctx: CanvasRenderingContext2D,
    viewport: Viewport,
    p1: Point,
    p2: Point,
): void {
    const start = viewport.worldToScreen(p1);
    const end = viewport.worldToScreen(p2);

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
    viewport: Viewport,
    p1: Point,
    p2: Point,
    color: string,
): void {
    const start = viewport.worldToScreen(p1);
    const end = viewport.worldToScreen(p2);

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

function drawCarriage(
    ctx: CanvasRenderingContext2D,
    viewport: Viewport,
    cPos: Point,
): void {
    const pos = viewport.worldToScreen(cPos);
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
    const viewport = new Viewport(
        { width: canvas.width, height: canvas.height },
        state.aPos,
    );

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    drawSupportLine(ctx, viewport, state.oPos, state.aPos);
    drawCable(ctx, viewport, state.oPos, state.cPos, '#3498db');
    drawCable(ctx, viewport, state.aPos, state.cPos, '#e74c3c');
    drawCarriage(ctx, viewport, state.cPos);
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
