const canvas = document.getElementById('graph');
const ctx = canvas.getContext('2d');
const logCurX = document.getElementById('screen-log-x');
const logCurY = document.getElementById('screen-log-y');
const coorTooltip = document.getElementById('coor-tooltip');

let realR = null;
let RDraw;
let step;
let offsetY = 0;

function drawSquare() {
    ctx.fillRect(0, 0, -RDraw/2, RDraw);
}

function drawCircle() {
    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.arc(0, 0, RDraw, 0, -Math.PI / 2, true);
    ctx.closePath();
    ctx.fill();
}

function drawTriangle() {
    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.lineTo(RDraw, 0);
    ctx.lineTo(0, RDraw/2);
    ctx.closePath();
    ctx.fill();
}

function drawNet() {
    const left = -canvas.width / 2;
    const right = canvas.width / 2;
    const top = canvas.height / 2;
    const bottom = -canvas.height / 2;

    const startX = Math.floor(left / step) * step;
    const endX = Math.ceil(right / step) * step;
    const startY = Math.floor(bottom / step) * step;
    const endY = Math.ceil(top / step) * step;

    ctx.save();
    ctx.beginPath();
    ctx.setLineDash([5, 10]);
    ctx.lineWidth = 1;
    ctx.strokeStyle = 'rgba(0, 0, 0, 0.3)';

    for (let x = startX; x <= endX; x += step) {
        if (Math.abs(x) < 0.1) continue;
        ctx.moveTo(x, bottom);
        ctx.lineTo(x, top);
    }

    for (let y = startY; y <= endY; y += step) {
        if (Math.abs(y) < 0.1) continue;
        ctx.moveTo(left, y);
        ctx.lineTo(right, y);
    }

    ctx.stroke();

    ctx.setLineDash([]);
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(left, 0);
    ctx.lineTo(right, 0);
    ctx.moveTo(0, bottom);
    ctx.lineTo(0, top);
    ctx.stroke();

    ctx.restore();
}

function drawLabels() {
    if (!realR) return;

    const fontSize = step / 4;
    ctx.save();
    ctx.scale(1, -1);
    ctx.font = `${fontSize}px Exo, sans-serif`;
    ctx.fillStyle = 'black';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    const Values = [-8, -7, -6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8];
    Values.forEach(val => {
        const x = val * step;
        if (Math.abs(x) < canvas.width / 2) {
            ctx.fillText(val.toString(), x, 20);
        }
    });

    ctx.textAlign = 'right';
    Values.forEach(val => {
        const y = val * step;
        if (Math.abs(y) < canvas.height / 2 && val !== 0) {
            ctx.fillText(val.toString(), -10, -y);
        }
    });

    ctx.restore();
}

function drawGraph() {
    canvas.width = canvas.clientWidth;
    canvas.height = canvas.clientHeight;

    step = Math.min(canvas.width, canvas.height) / 8;
    if (realR) {
        RDraw = realR * step;
    }

    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.translate(canvas.width / 2, canvas.height / 2 + offsetY);
    ctx.scale(1, -1);

    ctx.fillStyle = 'rgba(1, 50, 32, 0.6)';
    ctx.strokeStyle = 'rgba(1, 50, 32, 0.8)';
    ctx.lineWidth = 2;

    drawNet();

    if (realR && RDraw) {
        drawCircle();
        drawTriangle();
        drawSquare();
        drawPointsOperation();
    }

    drawLabels();
}

function curCoordinatesInLogicWorld(e) {
    const rect = canvas.getBoundingClientRect();
    const mouseXCanvas = e.clientX - rect.left;
    const mouseYCanvas = e.clientY - rect.top;
    const centerXCanvas = canvas.width / 2;
    const centerYCanvas = canvas.height / 2;

    let curX = (mouseXCanvas - centerXCanvas) / step;
    let curY = (centerYCanvas + offsetY - mouseYCanvas) / step;

    curX = Math.round(curX * 10000) / 10000;
    curY = Math.round(curY * 10000) / 10000;

    return { curX, curY };
}

function displayCurCoordinates(e) {
    if (!realR) {
        logCurX.innerText = "-.--";
        logCurY.innerText = "-.--";
        return;
    }

    const coords = curCoordinatesInLogicWorld(e);
    logCurX.innerText = coords.curX.toFixed(2);
    logCurY.innerText = coords.curY.toFixed(2);

    if (coorTooltip) {
        coorTooltip.textContent = `x: ${coords.curX.toFixed(2)}, y: ${coords.curY.toFixed(2)}`;
        coorTooltip.style.left = `${e.pageX + 15}px`;
        coorTooltip.style.top = `${e.pageY + 10}px`;
        if (e.pageX - window.innerWidth > -110) {
            coorTooltip.style.left = `${e.pageX - 100}px`;
        }
        coorTooltip.style.display = 'block';
    }
}

function updateGraphR() {
    setTimeout(() => {
        const checked = document.querySelector('input[name*=":R"]:checked');
        if (checked) {
            realR = parseFloat(checked.value);
            drawGraph();
        }
    }, 50);
}

canvas.addEventListener('click', e => {
    if (!realR) {
        showOverlay(2, "R undefined!")
        return;
    }

    const coords = curCoordinatesInLogicWorld(e);
    window.submitGraphPoint(coords.curX, coords.curY);
});

canvas.addEventListener('pointermove', displayCurCoordinates);

canvas.addEventListener('pointerleave', () => {
    logCurX.innerText = "-.--";
    logCurY.innerText = "-.--";
    if (coorTooltip) {
        coorTooltip.style.display = 'none';
    }
});