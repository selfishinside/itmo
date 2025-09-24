<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.project.utils.Point" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="author" content="Кара-Огланов Руслан Денисович">
        <meta name="description" content="Web-programming, lab2">
        <meta name="keywords" content="ITMO, Web-programming, VT">
        <link rel="stylesheet" href="resources/styles/stylesheets.css" type="text/css">
        <title>Лабораторная работа №2 | Веб - программирование</title>
    </head>
    <body>
        <header>
            <h1>Веб - программирование | P3207 | Вариант № 409431</h1>
            <h2>кара-огланов руслан денисович</h2>
        </header>
        <table id="mainTable">
            <tr>
                <td id="graph" class="content">
                <h2 class="title-plate">Граф</h2>  
                    <svg id="graph-svg" width="400" height="400" viewBox="-200 -200 400 400" xmlns="http://www.w3.org/2000/svg">
                        <line x1="-200" y1="0" x2="200" y2="0" stroke="black"></line> 
                        <line x1="0" y1="200" x2="0" y2="-200" stroke="black"></line> 
                        <line x1="-150" y1="-5" x2="-150" y2="5" stroke="black"></line>
                        <text x="-160" y="20" font-size="20">-R</text>
                        <line x1="-75" y1="-5" x2="-75" y2="5" stroke="black"></line>
                        <text x="-85" y="20" font-size="20">-R/2</text>
                        <line x1="150" y1="-5" x2="150" y2="5" stroke="black"></line>
                        <text x="140" y="20" font-size="20">R</text>
                        <line x1="75" y1="-5" x2="75" y2="5" stroke="black"></line>
                        <text x="65" y="20" font-size="20">R/2</text>                    
                        <line x1="-5" y1="150" x2="5" y2="150" stroke="black"></line>
                        <text x="10" y="155" font-size="20">-R</text>                    
                        <line x1="-5" y1="75" x2="5" y2="75" stroke="black"></line>
                        <text x="10" y="80" font-size="20">-R/2</text>                    
                        <line x1="-5" y1="-150" x2="5" y2="-150" stroke="black"></line>
                        <text x="10" y="-140" font-size="20">R</text>                    
                        <line x1="-5" y1="-75" x2="5" y2="-75" stroke="black"></line>
                        <text x="10" y="-65" font-size="20">R/2</text>
                        <!-- triangle -->
                        <polygon points="0,0 0, 75 -150, 0" fill-opacity="0.4" stroke="black" fill="purple"></polygon> 
                        <!-- sector -->
                        <path d="M 0 0 H 150 A -150 -150 0 0 1 0 150 V 0 " fill-opacity="0.4" stroke="black" fill="purple"></path>
                        <!-- square -->
                        <rect x="-150" y="-150" width="150" height="150" fill-opacity="0.4" stroke="black" fill="purple"></rect> 
                        <polygon points="200,0 190,5 190,-5" fill="black"></polygon>
                        <polygon points="0,-200 -5,-190 5,-190" fill="black"></polygon> 
                        <text x="180" y="20" font-size="20">X</text>
                        <text x="-40" y="-180" font-size="20">Y</text>
                    </svg>
                </td>
                <td class="content">
                    <h2 class="title-plate">Ввод данных</h2>
                    <form action="controller" id="input-form" method="post">
                        <!-- Поле для выбора X -->
                        <label for="x-select" class="data-label">X:</label>
                        <select name="x" id="x-select" required>
                            <option value="" disabled selected>Выберите значение</option>
                            <option value="-5">-5</option>
                            <option value="-4">-4</option>
                            <option value="-3">-3</option>
                            <option value="-2">-2</option>
                            <option value="-1">-1</option>
                            <option value="0">0</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                        </select>
                        <br>
                    
                        <!-- Поле для ввода Y -->
                        <label for="y" class="data-label">Y:</label>
                        <input type="text" id="y-value" name="y" placeholder="[-5:5]" required>
                        <br>
                    
                        <!-- Поле для ввода R -->
                        <label for="r" class="data-label">R:</label>
                        <input type="text" id="r-value" name="r" placeholder="[1:4]" required>
                        <br>
                    
                        <!-- Кнопка отправки -->
                        <button type="button" id="submit">Отправить</button>
                        
                        <!-- Сообщения об ошибках и предупреждениях -->
                        <div id="error-message" style="color: red;"></div>
                        <div id="warning-message" style="color: orange;"></div>
                    </form>
                    
                </td>
                
            </tr>
            <tr>
                <td id="results" class="content" colspan="2">
                    <h2 class="title-plate">Результаты</h2>
                    <jsp:include page="result.jsp"/>
                </td>
            </tr>
        </table>
        <form id="hidden-form" style="display: none;">
            <input type="hidden" id="hidden-x" name="x">
            <input type="hidden" id="hidden-y" name="y">
            <input type="hidden" id="hidden-r" name="r">
            <button type="submit" id="hidden-submit"></button>
        </form>
        <script src="resources/scripts/index.js"></script>
    </body>
</html>