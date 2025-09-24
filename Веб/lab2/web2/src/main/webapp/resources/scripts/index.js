document.getElementById("submit").addEventListener('click', function (event) {
    event.preventDefault();

    const xValue = validateX();
    const yValue = document.getElementById("y-value").value;
    const rValue = document.getElementById("r-value").value;

    if (!(xValue && validateY(yValue) && validateR(rValue))) return;

    if (!isPointInsideSVG(xValue, yValue, rValue)) {
        displayWarningMessage("Внимание: Точка выходит за границы графика!");
    } else {
        clearWarningMessage();
    }

    const queryParams = new URLSearchParams({
        'x': xValue,
        'y': yValue,
        'r': rValue
    }).toString();

    fetch(`check?${queryParams}`, {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => {
            updateTable(data);
            getValuesFromTable();
        })
        .catch(error => console.error('Ошибка:', error));
});

// Обработчик GET-запроса
function handleGetRequest() {
    const params = new URLSearchParams(window.location.search);

    const xValue = params.get('x');
    const yValue = params.get('y');
    const rValue = params.get('r');

    if (xValue && yValue && rValue) {
        if (!(validateXFromGet(xValue) && validateY(yValue) && validateR(rValue))) {
            displayErrorMessage("Некорректные параметры в строке запроса!");
            return;
        }

        fetch(`check?x=${xValue}&y=${yValue}&r=${rValue}`, {
            method: 'GET',
        })
            .then(response => response.json())
            .then(data => {
                updateTable(data);
                getValuesFromTable();
            })
            .catch(error => console.error('Ошибка:', error));
    }
}

// Обработчик кликов на графике
document.getElementById("graph-svg").addEventListener("click", function (event) {
    let r = parseFloat(document.getElementById("r-value").value);
    if (!r || isNaN(r)) {
        displayErrorMessage("Введите R перед кликом!");
        return;
    }

    const svg = event.currentTarget;
    const rect = svg.getBoundingClientRect();

    const clickX = event.clientX - rect.left;
    const clickY = event.clientY - rect.top;

    const scaleFactor = 150 / r;

    const x = ((clickX - 200) / scaleFactor);
    const y = -((clickY - 200) / scaleFactor);

    if (!isPointInsideSVG(x, y, r)) {
        displayWarningMessage("Точка вышла за границы графика!");
    } else {
        clearWarningMessage();
    }

    drawPoint(x, y, r, false);

    const queryParams = new URLSearchParams({
        'x': x,
        'y': y,
        'r': r
    }).toString();

    fetch(`check?${queryParams}`, {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => {
            updateTable(data);
            getValuesFromTable();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            displayErrorMessage("Произошла ошибка при отправке данных. Попробуйте снова.");
        });
});



// Логика переключения чекбоксов для X
const xCheckboxes = document.querySelectorAll("input[name='x']");

xCheckboxes.forEach(checkbox => {
    checkbox.addEventListener('click', function () {
        xCheckboxes.forEach(cb => {
            if (cb !== this) cb.checked = false; 
        });
    });
});

// Проверяет, находится ли точка внутри границ SVG
function isPointInsideSVG(x, y, r) {
    const graphMin = -200;
    const graphMax = 200;
    const scaleFactor = 150 / r;

    const scaledX = x * scaleFactor;
    const scaledY = -y * scaleFactor;

    return (scaledX >= graphMin && scaledX <= graphMax) && (scaledY >= graphMin && scaledY <= graphMax);
}

// Отрисовка точки на графике
function drawPoint(x, y, r, isHit) {
    const svg = document.getElementById("graph-svg");
    const scaleFactor = 150 / r;

    const scaledX = x * scaleFactor;
    const scaledY = -y * scaleFactor;

    const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    circle.setAttribute("cx", scaledX);
    circle.setAttribute("cy", scaledY);
    circle.setAttribute("r", 5);
    circle.setAttribute("fill", isHit ? "green" : "red");

    svg.appendChild(circle);
}

// Обновление таблицы
function updateTable(points) {
    const tableBody = document.getElementById('resultTable');
    while (tableBody.rows.length > 1) {
        tableBody.deleteRow(1);
    }
    points.forEach(point => {
        const newRow = document.createElement("tr");

        const xCell = document.createElement("td");
        xCell.textContent = point.x;
        newRow.appendChild(xCell);

        const yCell = document.createElement("td");
        yCell.textContent = formatNumber(point.y);
        newRow.appendChild(yCell);

        const rCell = document.createElement("td");
        rCell.textContent = formatNumber(point.r);
        newRow.appendChild(rCell);
        const execTimeCell = document.createElement("td");
        execTimeCell.textContent = point.executionTime;
        newRow.appendChild(execTimeCell);

        const resultCell = document.createElement("td");
        resultCell.textContent = point.isHit ? 'попал' : 'промазал';
        newRow.appendChild(resultCell);

        tableBody.appendChild(newRow);
    });
}

function formatNumber(value) {
    return Number.isInteger(value) ? `${value}.0` : value;
}

function getValuesFromTable() {
    clearPoints(); 
    const table = document.getElementById("resultTable"); 
    if (table.rows.length <= 1) return;
    const r = parseFloat(table.rows[1].cells[2].innerText); 
    if (!isNaN(r)) {
        for (let i = 1; i < table.rows.length; i++) {
            const row = table.rows[i];
            const x = parseFloat(row.cells[0].innerText);
            const y = parseFloat(row.cells[1].innerText);
            const result = row.cells[4].innerText === "попал";
            
            drawPoint(x, y, r, result);
        }
    }
}

function clearPoints() {
    const svg = document.getElementById("graph-svg"); 
    const circles = svg.querySelectorAll("circle"); 
    circles.forEach(circle => circle.remove());
}

function validateX() {
    document.getElementById("error-message").innerHTML = ''; 
    const xSelect = document.getElementById("x-select"); 
    const xValue = xSelect.value; 

    if (xValue === '') {
        displayErrorMessage("Выберите значение X");
        return false;
    }
    return xValue;
}


function validateY(y) {
    document.getElementById("error-message").innerHTML = '';
    if (!y) {
        displayErrorMessage("Введите значение Y");
        return false;
    } else if (isNaN(y) || y < -3 || y > 5) {
        displayErrorMessage("Y должен быть числом [-3;5]");
        return false;
    }
    return true;
}

function validateR(r) {
    document.getElementById("error-message").innerHTML = '';
    if (!r) {
        displayErrorMessage("Введите значение R");
        return false;
    } else if (isNaN(r) || r < 1 || r > 4) {
        displayErrorMessage("R должен быть числом [1;4]");
        return false;
    }
    return true;
}

function validateXFromGet(x) {
    const xValues = [-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5];
    if (!xValues.includes(parseFloat(x))) {
        displayErrorMessage("X должен быть [-5; 5]");
        return false;
    }
    return true;
}

function displayErrorMessage(errorMessage) {
    document.getElementById("error-message").innerHTML = errorMessage;
}

function displayWarningMessage(warningMessage) {
    document.getElementById("warning-message").innerHTML = warningMessage;
    document.getElementById("warning-message").style.color = "black";
}

function clearWarningMessage() {
    document.getElementById("warning-message").innerHTML = "";
}

// При загрузке страницы
window.onload = function() {
    handleGetRequest();
    getValuesFromTable();
};
