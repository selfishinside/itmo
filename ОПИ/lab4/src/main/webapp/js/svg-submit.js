function svgClick(event) {

    const svg = document.getElementById('svg');
    const rect = svg.getBoundingClientRect();
    const xClick = event.clientX - rect.left;
    const yClick = event.clientY - rect.top;

    let x = (xClick - centerX) / scale;
    let y = (centerY - yClick) / scale;
    updatePointer(x, y);

    document.getElementById("hidden-form:graph-x").value = x;
    document.getElementById("hidden-form:graph-y").value = y;
    document.getElementById("hidden-form:graph-r").value = getR();
    document.getElementById("hidden-form:graph-send").click();
}
document.getElementById('svg').addEventListener('click', svgClick);
