package com.example.project;

import com.example.project.utils.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.example.project.utils.Checker;
public class ResultsBean implements Serializable {
    private List<Point> points;

    public ResultsBean() {
        points = new ArrayList<>();
    }

    public List<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void updateAllPoints(float r) {
        for (Point point : points) {
            point.setR(r);
            point.setIsHit(Checker.isHit(point.getX(), point.getY(), point.getR()));
        }
    }
}
