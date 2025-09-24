'use strict';



/**
 * Center of x axis on svg graph. Equals half of svg.width
 * @type {number}
 */
const centerX = document.getElementById("svg").getAttribute('width') / 2; // Центр по оси X
/**
 * Center of y axis on svg graph. Equals half of svg.height
 * @type {number}
 */
const centerY = document.getElementById("svg").getAttribute('height') / 2; // Центр по оси Y
/**
 * This const sets a scale for svg elements
 * @type {number}
 */
const scale = 40;

function svgClick(event) {

    const svg = document.getElementById('svg');
    const rect = svg.getBoundingClientRect();
    const xClick = event.clientX - rect.left;
    const yClick = event.clientY - rect.top;

    let x = (xClick - centerX) / scale;
    let y = (centerY - yClick) / scale;

    document.getElementById("hidden-form:graph-x").value = x;
    document.getElementById("hidden-form:graph-y").value = y;
    document.getElementById("hidden-form:graph-r").value = getR();
    // sendPoint(x, y);
    document.getElementById("hidden-form:graph-send").click();
}

function addSvgListener() {
    console.log("start...")
    document.getElementById('svg').removeEventListener('click', svgClick);
    document.getElementById('svg').addEventListener('click', svgClick);
    console.log("end...")
}

window.onload = function() {
    addSvgListener();
}

function getR() {
    const r = PF('r').getSelectedValue();
    return r;
}



