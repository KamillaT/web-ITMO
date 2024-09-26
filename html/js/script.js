document.getElementById("rValue").addEventListener("change", drawGraph);

let xValue = null;

function drawGraph() {
    const r = document.getElementById("rValue").value;
    const canvas = document.getElementById("graphCanvas");
    const context = canvas.getContext("2d");

    // Очистим холст
    context.clearRect(0, 0, canvas.width, canvas.height);

    // Рисуем сетку
    drawGrid(context, canvas.width, canvas.height);

    // Рисуем оси
    drawAxes(context, canvas.width, canvas.height);

    // Добавляем метки
    drawLabels(context, canvas.width, canvas.height, r);

    // Устанавливаем прозрачность для фигур
    context.globalAlpha = 0.7;

    // Устанавливаем разные цвета для каждой фигуры
    const scale = 50;

    // 1. Прямоугольник с диагональными линиями (отличается от исходного)
    context.fillStyle = createGradient(context, canvas, 'red');
    context.fillRect(canvas.width / 2 - scale * r, canvas.height / 2 - scale * r / 2, scale * r, scale * r / 2);

    // 2. Четверть окружности с градиентом
    context.beginPath();
    context.fillStyle = createGradient(context, canvas, 'blue');
    context.moveTo(canvas.width / 2, canvas.height / 2);
    context.arc(canvas.width / 2, canvas.height / 2, scale * r, 1.5 * Math.PI, 2 * Math.PI, false);
    context.lineTo(canvas.width / 2, canvas.height / 2);
    context.fill();

    // 3. Треугольник с другим цветом
    context.beginPath();
    context.fillStyle = createGradient(context, canvas, 'green');
    context.moveTo(canvas.width / 2, canvas.height / 2);  // Центр
    context.lineTo(canvas.width / 2 + scale * r, canvas.height / 2);  // Правая точка
    context.lineTo(canvas.width / 2, canvas.height / 2 + scale * r);  // Нижняя точка
    context.closePath();
    context.fill();

    // Возвращаем прозрачность к норме для остальных элементов
    context.globalAlpha = 1.0;
}

// Функция для рисования осей
function drawAxes(context, width, height) {
    context.strokeStyle = "black";
    context.lineWidth = 2;

    // Оси X и Y
    context.beginPath();
    context.moveTo(width / 2, 0);  // Ось Y
    context.lineTo(width / 2, height);
    context.moveTo(0, height / 2);  // Ось X
    context.lineTo(width, height / 2);
    context.stroke();

    // Рисуем метки
    drawAxisTicks(context, width, height);
}

// Функция для рисования сетки
function drawGrid(context, width, height) {
    context.strokeStyle = "#ddd";
    context.lineWidth = 1;

    // Рисуем вертикальные и горизонтальные линии сетки
    const step = 50;  // Расстояние между линиями
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

// Функция для добавления меток на оси
function drawAxisTicks(context, width, height) {
    const scale = 50;

    // Метки на оси X
    for (let i = -scale * 7; i <= scale * 7; i += scale) {
        context.beginPath();
        context.moveTo(width / 2 + i, height / 2 - 5);
        context.lineTo(width / 2 + i, height / 2 + 5);
        context.stroke();
    }

    // Метки на оси Y
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
    context.fillStyle = "black";

    // Метки на оси X
    context.fillText("-R", width / 2 - scale * r, height / 2 + 15);
    context.fillText("-R/2", width / 2 - scale * r / 2, height / 2 + 15);
    context.fillText("R/2", width / 2 + scale * r / 2, height / 2 + 15);
    context.fillText("R", width / 2 + scale * r, height / 2 + 15);

    // Метки на оси Y
    context.fillText("-R", width / 2 + 5, height / 2 + scale * r);
    context.fillText("-R/2", width / 2 + 5, height / 2 + scale * r / 2);
    context.fillText("R/2", width / 2 + 5, height / 2 - scale * r / 2);
    context.fillText("R", width / 2 + 5, height / 2 - scale * r);
}

// Функция для создания градиента
function createGradient(context, canvas, color) {
    const gradient = context.createLinearGradient(0, 0, canvas.width, canvas.height);
    gradient.addColorStop(0, color);
    gradient.addColorStop(1, "white");
    return gradient;
}

function drawPoints(x, y, r) {
    const canvas = document.getElementById("graphCanvas");
    const context = canvas.getContext("2d");
    const scale = 50;

    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const pointX = centerX + x * scale;
    const pointY = centerY - y * scale;

    context.fillStyle = "pink";
    context.beginPath();
    context.arc(pointX, pointY, 3, 0, 2 * Math.PI);
    context.fill();
}

function setX(value) {
    xValue = Number(value);
    console.log("X set to: " + xValue);
}

function showNotification(message) {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.style.display = 'block';
    notification.style.opacity = 1;

    setTimeout(() => {
        notification.style.opacity = 0;
        setTimeout(() => {
            notification.style.display = 'none';
        }, 500);
    }, 3000);
}

function submitData() {
    const yText = document.getElementById('yValue').value;
    let yValue = 0;
    if (!/^-?\d+(\.\d+)?$/.test(yText) || yText < -5 || yText > 5) {
        showNotification("Please enter a valid Y coordinate between -5 and 5.");
        console.warn("Invalid Y value:", yText);
        return;
    } else {
        yValue = Number(yText);
    }
    const rValue = Number(document.getElementById('rValue').value);

    // Simple validation
    if (xValue === null || isNaN(yValue) || yValue < -5 || yValue > 5 || isNaN(rValue)) {
        showNotification("Please enter valid values for all fields.");
        return;
    }

    // Prepare the data in application/x-www-form-urlencoded format
    const data = JSON.stringify({x: xValue, y: yValue, r: rValue});

    fetch('/fcgi-bin/server.jar', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: data
    })
        .then(response => response.json())
        .then(json => {
            const resultBody = document.getElementById('results');
            const newRow = document.createElement('tr');

            newRow.innerHTML = `
              <td>${xValue}</td>
              <td>${yValue}</td>
              <td>${rValue}</td>
              <td>${json.result !== undefined ? (json.result ? 'true' : 'false') : 'undefined'}</td>
              <td>${json.currentTime !== undefined ? json.currentTime : 'undefined'}</td>
              <td>${json.executionTime !== undefined ? json.executionTime : 'undefined'}</td>
          `;

            resultBody.appendChild(newRow);
            saveResponseToLocalStorage(json);

        })
        .catch(error => console.error('Error:', error));

    // Optionally, draw the point on the graph after submission
    drawPoints(xValue, yValue, rValue);
}

function getResponsesFromLocalStorage() {
    let data = localStorage.getItem("data");
    if (data == null) {
        data = '[]';
    }
    const obj = JSON.parse(data);
    return Object.keys(obj).map((key) => obj[key]);
}

function saveResponseToLocalStorage(response) {
    let responses = getResponsesFromLocalStorage();
    responses.push(response);
    console.log('All responses:', responses);
    localStorage.setItem("data", JSON.stringify(responses));
}

function showResponse(response) {
    const resultBody = document.getElementById('results');
    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td>${response.x}</td>
        <td>${response.y}</td>
        <td>${response.r}</td>
        <td>${response.result !== undefined ? (response.result ? 'true' : 'false') : 'undefined'}</td>
        <td>${response.currentTime !== undefined ? response.currentTime : 'undefined'}</td>
        <td>${response.executionTime !== undefined ? response.executionTime : 'undefined'}</td>
    `;

    resultBody.appendChild(newRow);
}

function init() {
    let data = getResponsesFromLocalStorage();
    for (let i = 0; i < data.length; i++) {
        console.log("Loaded from local storage:", data[i]);
        showResponse(data[i]);
    }
}

// Call init on page load
//window.onload = init;

window.onload = function() {
    drawGraph();
    init();
};
