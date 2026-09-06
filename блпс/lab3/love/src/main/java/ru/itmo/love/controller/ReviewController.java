package ru.itmo.love.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.love.dto.common.ApiResponse;
import ru.itmo.love.dto.review.SubmitReviewRequest;
import ru.itmo.love.entity.Review;
import ru.itmo.love.service.ReviewServiceInt;

import java.util.List;

/**
 * rest api отправки рецензий и просмотра результата их асинхронной модерации
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceInt reviewService;

    /** принимает рецензию сохраняет ее и публикует kafka событие для consumer */
    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).REVIEW_SUBMIT)")
    public ResponseEntity<ApiResponse<Review>> submit(@RequestBody SubmitReviewRequest request) {
        try {
            Review review = reviewService.submit(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(ApiResponse.success(review, "Review accepted for asynchronous moderation"));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(exception.getMessage(), "REVIEW_REJECTED"));
        }
    }

    /** возвращает рецензии и их текущие статусы модерации */
    @GetMapping
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).REVIEW_READ)")
    public ResponseEntity<ApiResponse<List<Review>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getAll(), "Reviews found"));
    }
}
