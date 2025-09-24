package com.example.project.utils;

import java.time.LocalDateTime;

public class Point {
    private float x; 
    private float y; 
    private float r;
    private boolean isHit;
    private long executionTime;

    public Point(float x, float y, float r, boolean isHit, long executionTime) {
        this.x = x;
        this.y = y; 
        this.r = r;
        this.isHit = isHit;
        this.executionTime = executionTime;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getR() {
        return r;
    }
    public boolean getIsHit() {
        return isHit;
    }
    public long getExecutionTime() {
        return executionTime;
    }    
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setR(float r) {
        this.r = r;
    }
    public void setIsHit(boolean isHit) {
        this.isHit = isHit;
    }
}
