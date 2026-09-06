package ru.itmo.love.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.love.entity.enums.NotificationStatus;
import ru.itmo.love.entity.enums.NotificationType;

import java.time.LocalDateTime;

/**
 * уведомление гостя создаваемое асинхронным kafka consumer
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long guestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type; // sms email in_app

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status; // pending sent failed retry

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sentAt;

    /** реальный идентификатор задачи созданной для менеджера в bitrix24 */
    private String bitrixTaskId;

    @Column(nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

 // для идемпотентности
    @Column(unique = true)
    private String idempotencyKey; // booking_id event_id
}
