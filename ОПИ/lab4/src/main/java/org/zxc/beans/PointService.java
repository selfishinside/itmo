package org.zxc.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.zxc.dao.PointDao;
import org.zxc.models.Point;

@ApplicationScoped
public class PointService {
    private final PointDao pointDao = PointDao.getInstance();

    @Inject
    private PointTracker pointTracker;

    public void addPoint(Point point) {
        pointDao.addPoint(point); // Добавляем точку в БД
        pointTracker.checkPoint(point); // Обновляем MBean
    }
}