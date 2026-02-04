let hitPoints = [];
let missedPoints = [];

function distinguishPoints() {
    let arr = window.pointsXY || [];
    hitPoints = [];
    missedPoints = [];
    
    for (let i = 0; i < arr.length; i++) {
        if (isHit(arr[i].x, arr[i].y)) {
            hitPoints.push(arr[i]);
        } else {
            missedPoints.push(arr[i]);
        }
    }
}

function drawPointsOperation() {
    if (typeof window.pointsXY === 'undefined') return;
    distinguishPoints();
    drawPointsWithColor(hitPoints, 'green');
    drawPointsWithColor(missedPoints, 'red');
}

function drawPointsWithColor(points, type) {
    if (!Array.isArray(points) || points.length === 0) return;
    ctx.save();
    points.forEach(p => {
        ctx.beginPath();
        ctx.fillStyle = type;
        ctx.strokeStyle = type;
        const x = p.x * step;
        const y = p.y * step;
        const radius = 4;
        ctx.arc(x, y, radius, 0, 2 * Math.PI);
        ctx.fill();
    });
    ctx.restore();
}

function isHit(x, y) {
    return checkCircle(x, y) || checkSquare(x, y) || checkTriangle(x, y);
}

function checkSquare(x, y) {
    return x <= 0 && y >= 0 && x >= -realR / 2 && y <= realR;
}

function checkCircle(x, y) {
    return x >= 0 && y <= 0 && (x * x + y * y <= realR * realR);
}

function checkTriangle(x, y) {
    return x >= 0 && y >= 0 && ((x + 2*y) <= realR);
}