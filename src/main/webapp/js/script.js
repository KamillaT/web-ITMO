const points = [];

function showNotification(message, imageUrl = null) {
    const notification = document.getElementById('notification');

    notification.innerHTML = '';

    if (imageUrl) {
        const img = document.createElement('img');
        img.src = imageUrl;
        img.alt = 'Notification Image';
        img.style.width = '150px';
        img.style.height = '150px';
        img.style.marginRight = '10px';
        img.style.verticalAlign = 'middle';
        notification.appendChild(img);
    }

    const text = document.createElement('span');
    text.textContent = message;
    console.log(message, imageUrl);
    notification.appendChild(text);

    notification.style.display = 'flex';
    notification.style.opacity = 1;

    setTimeout(() => {
        notification.style.opacity = 0;
        setTimeout(() => {
            notification.style.display = 'none';
        }, 500);
    }, 3000);
}

function draw() {
    drawGraph();
    drawPointsFromDB();
}

function drawGraph() {
    const r = document.getElementById("rValue").value;
    const canvas = document.getElementById("graphCanvas");
    const context = canvas.getContext("2d");

    context.clearRect(0, 0, canvas.width, canvas.height);

    drawGrid(context, canvas.width, canvas.height);

    drawAxes(context, canvas.width, canvas.height);

    drawLabels(context, canvas.width, canvas.height, r);

    context.globalAlpha = 0.7;

    const scale = 50;

    context.fillStyle = createGradient(context, canvas, 'red');
    context.fillRect(canvas.width / 2 - scale * r, canvas.height / 2 - scale * r / 2, scale * r, scale * r / 2);

    context.beginPath();
    context.fillStyle = createGradient(context, canvas, 'blue');
    context.moveTo(canvas.width / 2, canvas.height / 2);
    context.arc(canvas.width / 2, canvas.height / 2, scale * r, -1.5 * Math.PI, -1 * Math.PI, false);
    context.lineTo(canvas.width / 2, canvas.height / 2);
    context.fill();

    context.beginPath();
    context.fillStyle = createGradient(context, canvas, 'rgb(249, 187, 55)');
    context.moveTo(canvas.width / 2, canvas.height / 2);
    context.lineTo(canvas.width / 2 + scale * r, canvas.height / 2);
    context.lineTo(canvas.width / 2, canvas.height / 2 + scale * r);
    context.closePath();
    context.fill();

    context.globalAlpha = 1.0;
}

function drawAxes(context, width, height) {
    context.strokeStyle = "white";
    context.lineWidth = 2;

    context.beginPath();
    context.moveTo(width / 2, 0);  // Ось Y
    context.lineTo(width / 2, height);
    context.moveTo(0, height / 2);  // Ось X
    context.lineTo(width, height / 2);
    context.stroke();

    drawAxisTicks(context, width, height);
}

function drawGrid(context, width, height) {
    context.strokeStyle = "rgba(255, 45, 203, 0.8)";
    context.lineWidth = 1;

    const step = 50;
    for (let x = 0; x < width; x += step) {
        context.beginPath();
        context.moveTo(x, 0);
        context.lineTo(x, height);
        context.stroke();
    }
    for (let y = 0; y < height; y += step) {
        context.beginPath();
        context.moveTo(0, y);
        context.lineTo(width, y);
        context.stroke();
    }
}

function drawAxisTicks(context, width, height) {
    const scale = 50;

    for (let i = -scale * 7; i <= scale * 7; i += scale) {
        context.beginPath();
        context.moveTo(width / 2 + i, height / 2 - 5);
        context.lineTo(width / 2 + i, height / 2 + 5);
        context.stroke();
    }

    for (let i = -scale * 7; i <= scale * 7; i += scale) {
        context.beginPath();
        context.moveTo(width / 2 - 5, height / 2 + i);
        context.lineTo(width / 2 + 5, height / 2 + i);
        context.stroke();
    }
}

function drawLabels(context, width, height, r) {
    const scale = 50;
    context.font = "14px Arial";
    context.fillStyle = "white";

    // X-axis
    context.fillText("-R", width / 2 - scale * r, height / 2 + 15);
    context.fillText("-R/2", width / 2 - scale * r / 2, height / 2 + 15);
    context.fillText("R/2", width / 2 + scale * r / 2, height / 2 + 15);
    context.fillText("R", width / 2 + scale * r, height / 2 + 15);

    // Y-axis
    context.fillText("-R", width / 2 + 5, height / 2 + scale * r);
    context.fillText("-R/2", width / 2 + 5, height / 2 + scale * r / 2);
    context.fillText("R/2", width / 2 + 5, height / 2 - scale * r / 2);
    context.fillText("R", width / 2 + 5, height / 2 - scale * r);
}

function createGradient(context, canvas, color) {
    const gradient = context.createLinearGradient(0, 0, canvas.width, canvas.height);
    gradient.addColorStop(0, color);
    gradient.addColorStop(1, "white");
    return gradient;
}

function drawPoints(x, y, color) {
    const canvas = document.getElementById("graphCanvas");
    const rect = canvas.getBoundingClientRect();
    const context = canvas.getContext("2d");
    const scale = 50;

    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const pointX = centerX + x * scale;
    const pointY = centerY - y * scale;

    context.fillStyle = color;
    context.beginPath();
    context.arc(pointX, pointY, 5, 0, 2 * Math.PI);
    context.fill();
}

function validateForm() {
    let xValue = document.forms["pointForm"]["xValue"].value;
    let xValueHidden = document.forms["pointForm"]["hiddenXValue"].value;

    if (isNaN(xValue) || xValue == null || xValue < -7 || xValue > 5 ||
        isNaN(xValueHidden) || xValueHidden == null || xValueHidden < -7 || xValueHidden > 5) {
        showNotification("Choose X between -7 and 5");
        return false;
    }

    let yValue = document.forms["pointForm"]["yValue"].value;
    if (isNaN(yValue) || yValue == null || yValue == "" || yValue < -5 || yValue > 5 ||
        !/^-?\d+(\.\d+)?$/.test(yValue)) {
        showNotification("Choose Y between -5 and 5");
        return false;
    }

    let rValue = document.forms["pointForm"]["rValue"].value;
    if (isNaN(rValue) || rValue == null || rValue < 1 || rValue > 6) {
        showNotification("Choose X between 1 and 6");
        return false;
    }

    return true;
}

function drawPointsFromDB() {
    if (pointsFromDB != null) {
        pointsFromDB.forEach(point => {
            const rValue = parseFloat(document.getElementById("rValue").value);
            if (parseFloat(point.r) === rValue) {
                const color = point.resultMessage === "Hit!" ? "rgb(23, 255, 0)" : "red";
                drawPoints(point.x, point.y, color);
            }
        });
    }
}

window.onload = function() {
    draw();
    const canvas = document.getElementById('graphCanvas');
    const ctx = canvas.getContext('2d');

    let xVal = 0;
    let yVal = 0;

    canvas.addEventListener('click', function(event) {
        const rect = canvas.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;

        xVal = (x - 400) / 50;
        yVal = (400 - y) / 50;

        document.forms["pointForm"]["hiddenXValue"].value = xVal.toFixed(2);
        document.forms["pointForm"]["yValue"].value = yVal.toFixed(2);
        document.forms["pointForm"]["rValue"].value = document.getElementById("rValue").value;

        if (validateForm()) {
            handleClickOnCanvas(x, y);
            setTimeout(function() {
                document.getElementById('checkButton').click();
            }, 100);
        }
    });

    function handleClickOnCanvas(x, y) {
        ctx.beginPath();
        ctx.arc(x, y, 5, 0, 2 * Math.PI);
        ctx.fillStyle = "blue";
        ctx.fill();
    }
};