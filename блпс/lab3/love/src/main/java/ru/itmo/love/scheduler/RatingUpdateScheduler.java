package ru.itmo.love.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itmo.love.service.ReviewServiceInt;

/**
 * периодически пересчитывает средние рейтинги отелей по опубликованным рецензиям
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RatingUpdateScheduler {

    private final ReviewServiceInt reviewService;

    /** пересчитывает рейтинги и возвращает количество обработанных отелей */
    @Scheduled(cron = "${app.scheduling.rating-update-cron}")
    public int recalculateRatings() {
        int count = reviewService.recalculateAllHotelRatings();
        log.info("Rating update scheduler processed {} hotels", count);
        return count;
    }
}
