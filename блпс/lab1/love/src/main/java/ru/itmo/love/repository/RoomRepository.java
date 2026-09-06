package ru.itmo.love.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.itmo.love.entity.Room;

import java.time.LocalDate;
import java.util.List;

// репозиторий комнат
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // возвращает комнаты отеля
    List<Room> findByHotelId(Long hotelId);

    // возвращает комнаты отеля по доступности
    List<Room> findByHotelIdAndAvailable(Long hotelId, Boolean available);

    // возвращает комнаты по типу
    List<Room> findByRoomType(String roomType);

    // возвращает доступные комнаты на даты
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.available = true " +
           "AND NOT EXISTS (SELECT 1 FROM Booking b WHERE b.room.id = r.id " +
           "AND b.status NOT IN ('CANCELLED', 'PAYMENT_TIMEOUT') " +
           "AND ((b.checkInDate <= :checkOut) AND (b.checkOutDate >= :checkIn)))")
    List<Room> findAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut);
}
