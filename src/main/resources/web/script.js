const canvas = document.getElementById('simCanvas');
const ctx = canvas.getContext('2d');

// Simulation settings
const scale = 15; // 15 pixels per meter
const padding = 50; // Padding from the edges of the screen

// Adjust canvas resolution
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

const socket = new WebSocket(`ws://${window.location.host}/data`);

socket.onmessage = (event) => {
    const state = JSON.parse(event.data);
    console.log(state)
    draw(state);
};

function draw(state) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // 1. oPos -> aPos (The main support line/span)
    drawSupportLine(state.oPos, state.aPos);

    // 2. oPos -> cPos (Left cable)
    drawCable(state.oPos, state.cPos, "#3498db");

    // 3. aPos -> cPos (Right cable)
    drawCable(state.aPos, state.cPos, "#e74c3c");

    // 4. Draw the Camera Carriage (Optional, for context)
    drawCarriage(state.cPos);
}

/**
 * Transforms simulation coordinates to Canvas pixel coordinates.
 * Inverts Y so that 0 is at the bottom.
 */
function toCanvas(point) {
    return {
        x: padding + (point.x * scale),
        y: canvas.height - padding - (point.y * scale)
    };
}

function drawSupportLine(p1, p2) {
    const start = toCanvas(p1);
    const end = toCanvas(p2);

    ctx.strokeStyle = "#555";
    ctx.setLineDash([5, 5]); // Dashed line for the span
    ctx.beginPath();
    ctx.moveTo(start.x, start.y);
    ctx.lineTo(end.x, end.y);
    ctx.stroke();
    ctx.setLineDash([]); // Reset to solid
}

function drawCable(p1, p2, color) {
    const start = toCanvas(p1);
    const end = toCanvas(p2);

    ctx.strokeStyle = color;
    ctx.lineWidth = 3;
    ctx.beginPath();
    ctx.moveTo(start.x, start.y);
    ctx.lineTo(end.x, end.y);
    ctx.stroke();

    // Draw anchor point
    ctx.fillStyle = "white";
    ctx.beginPath();
    ctx.arc(start.x, start.y, 4, 0, Math.PI * 2);
    ctx.fill();
}

function drawCarriage(cPos) {
    const pos = toCanvas(cPos);
    ctx.fillStyle = "#f1c40f";
    ctx.fillRect(pos.x - 10, pos.y - 5, 20, 10);

    // Label
    ctx.fillStyle = "white";
    ctx.font = "12px Arial";
    ctx.fillText(`Cam (${cPos.x.toFixed(1)}, ${cPos.y.toFixed(1)})`, pos.x - 20, pos.y - 15);
}
