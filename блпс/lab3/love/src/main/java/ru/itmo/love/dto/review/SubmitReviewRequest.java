package ru.itmo.love.dto.review;

import lombok.Data;

/**
 * данные новой рецензии гостя после завершенного проживания
 */
@Data
public class SubmitReviewRequest {
    private Long bookingId;
    private Integer rating;
    private String title;
    private String comment;
}
