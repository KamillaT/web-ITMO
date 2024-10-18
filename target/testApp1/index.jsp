<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="utils.Result" %>
<html>
<head>
    <title>Проверка попадания в область</title>
    <link rel="stylesheet" href="css/style.css">
    <%
        List<Result> results = (List<Result>) request.getAttribute("results");
    %>
    <script type="text/javascript">
        document.addEventListener("DOMContentLoaded", function () {
            document.getElementById("rValue").addEventListener("change", draw);
        });

        const points = [];

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

        function draw() {
            drawGraph();
            <% if (results != null && results.size() > 0) { %>
                points.forEach(point => {
                    console.log(point.hit);
                    if (point.hit) {
                        drawPoints(point.x, point.y, "rgb(23, 255, 0)");
                    } else {
                        drawPoints(point.x, point.y, "red");
                    }
                });
            <% } %>
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
            context.arc(canvas.width / 2, canvas.height / 2, scale * r, 1.5 * Math.PI, 2 * Math.PI, false);
            context.lineTo(canvas.width / 2, canvas.height / 2);
            context.fill();

            context.beginPath();
            context.fillStyle = createGradient(context, canvas, 'rgb(249, 187, 55)');
            context.moveTo(canvas.width / 2, canvas.height / 2);  // Центр
            context.lineTo(canvas.width / 2 + scale * r, canvas.height / 2);  // Правая точка
            context.lineTo(canvas.width / 2, canvas.height / 2 + scale * r);  // Нижняя точка
            context.closePath();
            context.fill();

            context.globalAlpha = 1.0;
        }

        function drawAxes(context, width, height) {
            context.strokeStyle = "black";
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
            context.strokeStyle = "rgba(151, 0, 255, 0.70)";
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
            context.fillStyle = "black";

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

        let xValue = null;

        function setXValue(value) {
            xValue = Number(value);
            console.log("X set to: " + xValue);
        }

        function validateForm() {
            document.forms["pointForm"]["x"].value = xValue;
            if (isNaN(xValue) || xValue == null || xValue < -5 || xValue > 3) {
                showNotification("Выберите X в диапазоне от -5 до 3");
                return false;
            }

            let yValue = document.forms["pointForm"]["y"].value;
            if (isNaN(yValue) || yValue == null || yValue == "" || yValue < -5 || yValue > 5 ||
                !/^-?\d+(\.\d+)?$/.test(yValue)) {
                showNotification("Y должен быть числом в диапазоне от -5 до 5");
                return false;
            }

            let rValue = document.forms["pointForm"]["r"].value;
            if (isNaN(rValue) || rValue == null || rValue < 1 || rValue > 5) {
                showNotification("R должен быть числом в диапазоне от 1 до 5");
                return false;
            }

            return true;
        }
        window.onload = function() {
            drawGraph();
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

               document.forms["pointForm"]["x"].value = xVal.toFixed(2);
               document.forms["pointForm"]["y"].value = yVal.toFixed(2);
               document.forms["pointForm"]["r"].value = document.getElementById("rValue").value;

                handleClickOnCanvas(x, y);
                setTimeout(function() {
                        document.forms["pointForm"].submit();
                    }, 100);
            });

            function handleClickOnCanvas(x, y) {
                ctx.beginPath();
                ctx.arc(x, y, 5, 0, 2 * Math.PI);
                ctx.fillStyle = "blue";
                ctx.fill();
            }

            <% if (results != null && results.size() > 0) { %>
                <% for (Result result : results) { %>
                    points.push({x: <%= result.getX() %>, y: <%= result.getY() %>, hit: <%= result.isHit() %>});
                <% } %>

                points.forEach(point => {
                    if (point.hit) {
                        drawPoints(point.x, point.y, "rgb(23, 255, 0)");
                    } else {
                        drawPoints(point.x, point.y, "red");
                    }
                });
            <% } %>
        };
    </script>
</head>
<body>
    <h1>Проверка попадания точки</h1>
    <label>Камилла P3223</label>
    <label class="rightLabel">409660</label>
    <div>
        <form name="pointForm" action="controller" method="post" onsubmit="return validateForm();">
            <table class="headTable">
                <thead>
                    <tr>
                        <th>X</th>
                        <th>Y</th>
                        <th>R</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            <input type="hidden" name="x" id="xValue" value=""/>

                            <input type="button" class="roundBtn" value="-5" onclick="setXValue('-5')"/>
                            <input type="button" class="roundBtn" value="-4" onclick="setXValue('-4')"/>
                            <input type="button" class="roundBtn" value="-3" onclick="setXValue('-3')"/>
                            <input type="button" class="roundBtn" value="-2" onclick="setXValue('-2')"/>
                            <input type="button" class="roundBtn" value="-1" onclick="setXValue('-1')"/>
                            <input type="button" class="roundBtn" value="0" onclick="setXValue('0')"/>
                            <input type="button" class="roundBtn" value="1" onclick="setXValue('1')"/>
                            <input type="button" class="roundBtn" value="2" onclick="setXValue('2')"/>
                            <input type="button" class="roundBtn" value="3" onclick="setXValue('3')"/>
                        </td>
                        <td>
                            <input name="y" type="number" min="-5" max="5" placeholder="Number from -5 to 5" style="background-color: rgba(219, 184, 243, 0.65);">
                        </td>
                        <td>
                            <select name="r" id="rValue" style="background-color: rgba(219, 184, 243, 0.65);">
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                            </select>
                        </td>
                    </tr>
                </tbody>
            </table>
            <input type="submit" class="btn" value="Проверить">
        </form>
        <a href="/testApp/clear"><button class="btn">Очистить историю</button></a>
        </div>

    <table class="twoTables">
        <tbody>
            <tr>
                <td>
                    <canvas id="graphCanvas" width="800" height="800"></canvas>
                </td>
            </tr>
        </tbody>
    </table>

    <div class="twoTables" id="overflow">
        <table style="width: 100%;">
            <thead>
                <tr>
                    <th>X</th>
                    <th>Y</th>
                    <th>R</th>
                    <th>Попадание</th>
                    <th>Дата</th>
                    <th>Время выполнения</th>
                </tr>
            </thead>
            <tbody id="results">
                <%
                    if (results != null && results.size() > 0) {
                        for (Result result : results) {
                %>
                            <tr>
                                <td><%= result.getX() %></td>
                                <td><%= result.getY() %></td>
                                <td><%= result.getR() %></td>
                                <td style="<%= result.isHit() ? "color: green;" : "color: red;" %>">
                                    <%= result.isHit() ? "Попадание" : "Мимо" %>
                                </td>
                                <td><%= result.getDate() %></td>
                                <td><%= result.getExecutionTime() %></td>
                            </tr>
                <%
                        }
                    } else {
                %>
                        <tr><td colspan="6" style="text-align: center;">Нет данных для отображения</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
    <table>
        <tr>
            <td id="notification" class="notification" style="display: none;"></td>
        </tr>
    </table>
</body>
</html>
