import type { CableCamState } from './types.js';
import { CablesLayer } from './view/layers/cables.js';
import { CarriageLayer } from './view/layers/carriage.js';
import { MovementVectorLayer } from './view/layers/movement-vector.js';
import { SupportLineLayer } from './view/layers/support-line.js';
import { Renderer } from './view/renderer.js';

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

const canvas = getCanvas();
const ctx = get2DContext(canvas);

canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

const renderer = new Renderer(ctx, canvas, [
    new SupportLineLayer(),
    new CablesLayer(),
    new CarriageLayer(),
    new MovementVectorLayer(),
]);

// The WebSocket pushes ~60 messages per second, but we render on the browser's
// frame clock so resize/animation/interaction work doesn't have to wait for the
// next message. The handler just publishes the latest snapshot; the rAF loop reads it.
let latestState: CableCamState | null = null;

const socket = new WebSocket(`ws://${window.location.host}/data`);
socket.addEventListener('message', (event: MessageEvent<string>) => {
    latestState = JSON.parse(event.data) as CableCamState;
});

function tick(): void {
    if (latestState !== null) {
        renderer.draw(latestState);
    }
    requestAnimationFrame(tick);
}
requestAnimationFrame(tick);
