package ru.itmo.love.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itmo.love.service.NotificationServiceInt;

/**
 * периодически повторяет отправку уведомлений со статусом failed
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRetryScheduler {

    private final NotificationServiceInt notificationService;

    /** повторяет неуспешные отправки и возвращает количество обработанных уведомлений */
    @Scheduled(fixedRateString = "${app.scheduling.notification-retry-rate}")
    public int retryFailedNotifications() {
        int count = notificationService.retryFailed();
        log.info("Notification retry scheduler processed {} notifications", count);
        return count;
    }
}
