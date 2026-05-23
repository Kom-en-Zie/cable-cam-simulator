import type { CableCamState } from './types.js';
import { CablesLayer } from './view/layers/cables.js';
import { CarriageLayer } from './view/layers/carriage.js';
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
]);

const socket = new WebSocket(`ws://${window.location.host}/data`);
socket.addEventListener('message', (event: MessageEvent<string>) => {
    const state = JSON.parse(event.data) as CableCamState;
    renderer.draw(state);
});
