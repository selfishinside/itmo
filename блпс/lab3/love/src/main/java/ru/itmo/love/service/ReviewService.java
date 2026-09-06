package ru.itmo.love.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.love.dto.review.SubmitReviewRequest;
import ru.itmo.love.entity.Booking;
import ru.itmo.love.entity.enums.BookingStatus;
import ru.itmo.love.entity.Review;
import ru.itmo.love.entity.enums.ReviewStatus;
import ru.itmo.love.event.ReviewSubmittedEvent;
import ru.itmo.love.integration.bitrix.BitrixPayload;
import ru.itmo.love.repository.ReviewRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * сервис рецензий
 */
@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewServiceInt {

    private final ReviewRepository reviewRepository;
    private final BookingServiceInt bookingService;
    private final HotelServiceInt hotelService;
    private final EventPublisherInt eventPublisher;
    private final BitrixConnectorInt bitrixConnector;

    /** создает рецензию */
    @Override
    @Transactional
    public Review submit(SubmitReviewRequest request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Booking booking = bookingService.getBookingEntityById(request.getBookingId());
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Review can only be submitted for a completed booking");
        }
        if (!reviewRepository.findByBookingId(booking.getId()).isEmpty()) {
            throw new IllegalStateException("Review for this booking already exists");
        }

        Review review = reviewRepository.save(Review.builder()
                .bookingId(booking.getId())
                .guestId(booking.getGuest().getId())
                .hotelId(booking.getRoom().getHotel().getId())
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .status(ReviewStatus.PENDING_MODERATION)
                .createdAt(LocalDateTime.now())
                .build());

        String eventId = UUID.randomUUID().toString();
        review.setIdempotencyKey("review-submitted:" + review.getId() + ":" + eventId);
        review = reviewRepository.save(review);
        eventPublisher.publishReviewSubmitted(new ReviewSubmittedEvent(
                eventId, review.getId(), review.getBookingId(), review.getHotelId(), LocalDateTime.now()));
        return review;
    }

    /** модерирует рецензию */
    @Override
    @Transactional
    public Review moderate(ReviewSubmittedEvent event) {
        Review review = reviewRepository.findById(event.reviewId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        if (review.getStatus() != ReviewStatus.PENDING_MODERATION) {
            return review;
        }

        boolean rejected = review.getComment() != null
                && review.getComment().toLowerCase().contains("spam");
        review.setStatus(rejected ? ReviewStatus.REJECTED : ReviewStatus.APPROVED);
        review.setModeratedAt(LocalDateTime.now());

        if (!rejected) {
            var result = bitrixConnector.send(new BitrixPayload("REVIEW", review.getId(),
                    "PUBLISH_REVIEW", Map.of("rating", review.getRating(), "hotelId", review.getHotelId())));
            if (!result.success()) {
                throw new IllegalStateException("Bitrix rejected review: " + result.response());
            }
            review.setBitrixDealId(result.externalId());
            review.setStatus(ReviewStatus.PUBLISHED);
        }
        Review saved = reviewRepository.save(review);
        recalculateHotelRating(review.getHotelId());
        return saved;
    }

    /**
     * заново кидает рецензию в kafka
     * нужно для повторного показа
     */
    @Override
    @Transactional
    public Review requeueForDemo(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        String eventId = UUID.randomUUID().toString();

        review.setStatus(ReviewStatus.PENDING_MODERATION);
        review.setModeratedAt(null);
        review.setBitrixDealId(null);
        review.setIdempotencyKey("review-submitted:" + review.getId() + ":" + eventId);
        Review saved = reviewRepository.save(review);

        eventPublisher.publishReviewSubmitted(new ReviewSubmittedEvent(
                eventId, saved.getId(), saved.getBookingId(), saved.getHotelId(), LocalDateTime.now()));
        return saved;
    }

    /** пересчитывает рейтинги */
    @Override
    @Transactional
    public int recalculateAllHotelRatings() {
        List<Long> hotelIds = hotelService.getAllHotelIds();
        hotelIds.forEach(this::recalculateHotelRating);
        return hotelIds.size();
    }

    /** отдает все рецензии */
    @Override
    @Transactional(readOnly = true)
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    private void recalculateHotelRating(Long hotelId) {
        List<Review> published = reviewRepository.findByHotelId(hotelId).stream()
                .filter(review -> review.getStatus() == ReviewStatus.PUBLISHED)
                .toList();
        BigDecimal averageRating = null;
        if (published.isEmpty()) {
            averageRating = null;
        } else {
            double average = published.stream().mapToInt(Review::getRating).average().orElse(0);
            averageRating = BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);
        }
        hotelService.updateReviewRating(hotelId, averageRating, published.size());
    }
}
