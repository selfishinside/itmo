package ru.itmo.love.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.love.entity.Notification;
import ru.itmo.love.entity.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
/**
 * репозиторий уведомлений с запросами для идемпотентности и повторной отправки
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /** возвращает уведомления с указанным статусом */
    List<Notification> findByStatus(NotificationStatus status);
    /** возвращает старые уведомления с указанным статусом */
    List<Notification> findByStatusAndCreatedAtBefore(NotificationStatus status, LocalDateTime dateTime);
    /** возвращает уведомления конкретного бронирования */
    List<Notification> findByBookingId(Long bookingId);
    /** ищет уже обработанное событие по уникальному ключу */
    Optional<Notification> findByIdempotencyKey(String idempotencyKey);
}
