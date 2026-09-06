package ru.itmo.love.service;

import ru.itmo.love.dto.review.SubmitReviewRequest;
import ru.itmo.love.entity.Review;
import ru.itmo.love.event.ReviewSubmittedEvent;

import java.util.List;

/**
 * контракт рецензий
 */
public interface ReviewServiceInt {

    /** принимает рецензию */
    Review submit(SubmitReviewRequest request);

    /** модерирует рецензию */
    Review moderate(ReviewSubmittedEvent event);

    /** снова кидает в kafka */
    Review requeueForDemo(Long reviewId);

    /** пересчитывает рейтинги */
    int recalculateAllHotelRatings();

    /** все рецензии */
    List<Review> getAll();
}
