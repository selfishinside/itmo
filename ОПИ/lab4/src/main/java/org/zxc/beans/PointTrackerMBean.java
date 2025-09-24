package org.zxc.beans;

public interface PointTrackerMBean {
    long getTotalPoints();
    long getMissedPoints();
    void incrementPoints(boolean isHit);
}