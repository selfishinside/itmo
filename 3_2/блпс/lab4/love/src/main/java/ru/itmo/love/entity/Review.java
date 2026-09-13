package ru.itmo.love.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.love.entity.enums.ReviewStatus;

import java.time.LocalDateTime;

/**
 * рецензия гостя проходящая асинхронную модерацию после завершения бронирования
 */
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long guestId;

    @Column(nullable = false)
    private Long hotelId;

    @Column(nullable = false)
    private Integer rating; // 1 5

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status; // pending_moderation approved rejected published

    private String bitrixDealId; // id в bitrix

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime moderatedAt;

 // для идемпотентности
    @Column(unique = true)
    private String idempotencyKey; // review_id event_id
}
