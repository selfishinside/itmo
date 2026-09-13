package ru.itmo.love.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.love.entity.Review;
import ru.itmo.love.entity.enums.ReviewStatus;

import java.util.List;
import java.util.Optional;

@Repository
/**
 * репозиторий рецензий с запросами для модерации и расчета рейтинга
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /** возвращает рецензии отеля */
    List<Review> findByHotelId(Long hotelId);
    /** возвращает рецензии с указанным статусом */
    List<Review> findByStatus(ReviewStatus status);
    /** возвращает рецензии гостя */
    List<Review> findByGuestId(Long guestId);
    /** возвращает рецензии бронирования */
    List<Review> findByBookingId(Long bookingId);
    /** ищет уже принятое событие по уникальному ключу */
    Optional<Review> findByIdempotencyKey(String idempotencyKey);
}
