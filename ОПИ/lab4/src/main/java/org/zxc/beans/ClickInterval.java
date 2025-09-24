package org.zxc.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Named("ClickInterval")
@ApplicationScoped
public class ClickInterval implements Serializable, ClickIntervalMBean {

    private final AtomicLong lastClickTime = new AtomicLong();
    private final AtomicLong clickCount = new AtomicLong();
    private final AtomicReference<Double> averageInterval = new AtomicReference<>(0.0);
    private boolean isFirstClick = true;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object unused) {
        org.zxc.beans.MBeanRegistryUtil.registerBean(this, "ClickInterval");
    }

    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object unused) {
        MBeanRegistryUtil.unregisterBean(this);
    }

    public void recordClick() {
        long now = System.currentTimeMillis();
        if (isFirstClick) {
            lastClickTime.set(now);
            isFirstClick = false;
            return;
        }
        long past = lastClickTime.getAndSet(now);
        long interval = now - past;
        long count = clickCount.incrementAndGet();
        averageInterval.updateAndGet(cur -> (cur * (count - 1) + interval) / count);
    }


    @Override
    public double getClickInterval() {
        return averageInterval.get() / 1000;
    }

    @Override
    public long getClickCount() {
        return clickCount.get();
    }

    @Override
    public void resetStat() {
        lastClickTime.set(0);
        clickCount.set(0);
        averageInterval.set(0.0);
    }
}
