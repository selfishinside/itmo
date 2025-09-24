<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.project.utils.Point" %>
<%@ page import="java.util.ArrayList" %>

<jsp:useBean id="results" class="com.example.project.ResultsBean" scope="session"/>


<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Лабораторная Работа №2 | Веб - Программирование </title>
        <link rel="stylesheet" href="resources/styles/stylesheets.css" type="text/css">
    </head>
    <body>
        <table id="resultTable">
            <tr>
                <th>X</th>
                <th>Y</th>
                <th>R</th>
                <th>Время исполнения</th>
                <th>Результат попадания</th>
            </tr>
            <%
                List<Point> points = results.getPoints();
                if (points != null && !points.isEmpty()) {
                    for (Point point : points) {
            %>
                        <tr>
                            <td><%= point.getX() %></td>
                            <td><%= point.getY() %></td>
                            <td><%= point.getR() %></td>
                            <td><%= point.getExecutionTime() %></td>
                            <td><%= point.getIsHit() ? "попал" : "промазал" %></td>
                        </tr>
            <%
                    }
                }
            %>
        </table>
    </body>  
</html>
