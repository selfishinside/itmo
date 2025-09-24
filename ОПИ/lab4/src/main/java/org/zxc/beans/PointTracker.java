package org.zxc.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.zxc.dao.PointDao;
import org.zxc.models.Point;

import javax.management.*;
import java.io.Serializable;

@Named("pointTracker")
@ApplicationScoped
public class PointTracker extends NotificationBroadcasterSupport implements Serializable, PointTrackerMBean {

    private int sequenceNumber = 0;

    @Inject
    private PointDao pointDao;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object unused) {
        MBeanRegistryUtil.registerBean(this, "pointTracker");
    }

    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object unused) {
        MBeanRegistryUtil.unregisterBean(this);
    }

    @Override
    public long getTotalPoints() {
        return pointDao.getPoints().size();
    }

    @Override
    public long getMissedPoints() {
        return pointDao.getPoints().stream()
                .filter(p -> !inArea(p))
                .count();
    }

    @Override
    public void incrementPoints(boolean isHit) {
        long newTotal = getTotalPoints();
        if (newTotal % 10 == 0) {
            sendNotification(new Notification(
                    "PointsMultipleOfTen",
                    this,
                    sequenceNumber++,
                    System.currentTimeMillis(),
                    "Total points reached " + newTotal + " (multiple of 10)"
            ));
        }
    }

    public void checkPoint(Point point) {
        boolean inArea = inArea(point);
        incrementPoints(inArea);
    }

    private boolean inArea(Point point) {
        return point.calculate();
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[]{"PointsMultipleOfTen"};
        String name = Notification.class.getName();
        String description = "Notification when total points count is a multiple of 10";
        return new MBeanNotificationInfo[]{
                new MBeanNotificationInfo(types, name, description)
        };
    }
}