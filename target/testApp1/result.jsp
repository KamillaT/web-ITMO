<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Результат проверки</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <h1>Результат</h1>
    <table class="twoTables">
        <tr><td style="background-color: rgba(219, 184, 243, 0.65);">X:</td><td><%= request.getAttribute("x") %></td></tr>
        <tr><td style="background-color: rgba(219, 184, 243, 0.65);">Y:</td><td><%= request.getAttribute("y") %></td></tr>
        <tr><td style="background-color: rgba(219, 184, 243, 0.65);">Радиус:</td><td><%= request.getAttribute("r") %></td></tr>
        <tr>
            <td style="background-color: rgba(219, 184, 243, 0.65);">Попадание:</td>
            <td style="<%= request.getAttribute("hit").equals(true) ? "color: green;" : "color: red;" %>">
                <%= request.getAttribute("hit").equals(true) ? "Попадание" : "Мимо" %>
            </td>
        </tr>
        <tr><td style="background-color: rgba(219, 184, 243, 0.65);">Дата:</td><td><%= request.getAttribute("currentTime") %></td></tr>
        <tr><td style="background-color: rgba(219, 184, 243, 0.65);">Время выполнения:</td><td><%= request.getAttribute("executionTime") %></td></tr>
        <tr><td><a href="/testApp/controller"><button class="btn">Новый запрос</button></a></td><td><a href="/testApp/clear"><button class="btn">Очистить историю</button></a></td></tr>
    </table>
    <table class="twoTables" style="width: auto;">
        <%
            if (request.getAttribute("hit").equals(true)) {
        %>
            <tr><td><img src="gifs/happy.gif"></td></tr>
        <%
            } else {
        %>
            <tr><td><img src="gifs/sad.gif"></td></tr>
        <%
            }
        %>
    </table>
</body>
</html>
