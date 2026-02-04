const box = document.getElementById('result-overlay');
const text = document.getElementById('result-overlay-text');

let overlayTimer= null;

window.loadPointsFromJson = function(json) {
    try {
        const arr = (typeof json === 'string') ? JSON.parse(json) : json;
        if (!Array.isArray(arr)) {
            console.warn('Invalid points JSON');
            return;
        }
        
        window.pointsXY = arr
            .map(p => ({ 
                x: Number(p.x), 
                y: Number(p.y) 
            }))
            .filter(p => Number.isFinite(p.x) && Number.isFinite(p.y));
        
        if (typeof drawGraph === 'function') {
            drawGraph();
        }
    } catch (e) {
        console.error('Failed to parse points JSON', e);
    }
};

function showResultOverlay(hit) {
    if (!box || !text) {
        return;
    }

    if (overlayTimer) {
        clearTimeout(overlayTimer);
        overlayTimer = null;
    }

    box.classList.remove('hit', 'miss', 'error', 'show');

    if  (hit === 'error') {
        text.textContent = 'UNDEFINED FIELD(S)';
        box.classList.add('error');
    } else {
        text.textContent = hit ? 'HIT' : 'MISS';
        box.classList.add(hit ? 'hit' : 'miss');
    }

    void box.offsetWidth;
    box.classList.add('show');

    overlayTimer = setTimeout(() => {
        box.classList.remove('show');
    }, 2000);
}

function submitGraphPoint(x, y) {
    const gx = document.getElementById('graphForm:gx');
    const gy = document.getElementById('graphForm:gy');
    const btn = document.getElementById('graphForm:graphSubmit');

    gx.value = x;
    gy.value = y;

    btn.click();
}

document.addEventListener('DOMContentLoaded', () => {
    window.addEventListener('resize', drawGraph);

    document.addEventListener('change', (e) => {
        const target = e.target;
        if (target.matches('input[name*=":R"]')) {
            const value = parseFloat(target.value);
            if (!isNaN(value)) {
                realR = value;
                if (typeof drawGraph === 'function') {
                    setTimeout(drawGraph, 50);
                }
            }
        }
    }, true);

    const yInput = document.querySelector('input[id*=":y"]');
    if (yInput) {
        yInput.addEventListener('keypress', e => {
            if (e.key === 'Enter') {
                e.preventDefault();
            }
        });
    }

    setTimeout(() => {
        if (typeof drawGraph === 'function') {
            drawGraph();
        }
    }, 50);
});

window.addEventListener('load', () => {
    console.log('Application initialized');
    const checkedR = document.querySelector('input[id*=":R"]:checked');
    if (checkedR) {
        realR = parseFloat(checkedR.value);
    } else {
        const input1 = document.querySelector('input[name*="R"][value="1"]');
        if (input1) {
            const uiBox = input1.closest('.ui-radiobutton').querySelector('.ui-radiobutton-box');
            if (uiBox) {
                uiBox.click();
            }
        }
    }

    if (typeof drawGraph === 'function') {
        setTimeout(drawGraph, 300);
    }
});
