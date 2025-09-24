package com.example.project;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.example.project.utils.Checker;
import com.example.project.utils.Point;

@WebServlet("/check")
public class AreaCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            float x = Float.parseFloat(request.getParameter("x"));
            float y = Float.parseFloat(request.getParameter("y"));
            float r = Float.parseFloat(request.getParameter("r"));
    
            HttpSession session = request.getSession();
            ResultsBean results = (ResultsBean) session.getAttribute("results");
    
            if (results == null) {
                results = new ResultsBean();
                session.setAttribute("results", results);
            }
    
            if (!results.getPoints().isEmpty() && results.getPoints().get(0).getR() != r) {
                results.updateAllPoints(r);
            } else {
                addNewPoint(results, x, y, r);
            }
    
            sendResponse(response, results);
        } catch (IOException | NumberFormatException exception) {
            exception.printStackTrace();
        }
    }

    private void addNewPoint(ResultsBean results, float x, float y, float r) {
        long startTime = System.nanoTime();
        boolean isHit = Checker.isHit(x, y, r);
        long endTime = System.nanoTime();
        results.addPoint(new Point(x, y, r, isHit, endTime - startTime));
    }

    private void sendResponse(HttpServletResponse response, ResultsBean results) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(results.getPoints());

        PrintWriter writer = response.getWriter();
        writer.println(jsonResponse);
        writer.flush();
    }
}
