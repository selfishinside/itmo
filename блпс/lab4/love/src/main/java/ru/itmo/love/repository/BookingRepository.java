package ru.itmo.love.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itmo.love.entity.Booking;
import ru.itmo.love.entity.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

 // репозиторий бронирований
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
 // возвращает бронирования гостя
    List<Booking> findByGuestId(Long guestId);

 // возвращает бронирования комнаты
    List<Booking> findByRoomId(Long roomId);

 // возвращает бронирования по статусу
    List<Booking> findByStatus(BookingStatus status);

 // возвращает бронирования за период
    List<Booking> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

 // возвращает бронирования до даты
    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime dateTime);

 // возвращает бронирования отеля
    List<Booking> findByRoomHotelId(Long hotelId);

 // ищет подтвержденные бронирования с заселением в указанную дату
    List<Booking> findByStatusAndCheckInDate(BookingStatus status, LocalDate checkInDate);

 // ищет пересечения бронирований
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.status IN :statuses AND " +
            "((b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate))")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                          @Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate,
                                          @Param("statuses") List<BookingStatus> statuses);
}
