package ru.itmo.love.service;

import ru.itmo.love.dto.hotel.HotelDTO;
import ru.itmo.love.dto.hotel.HotelSearchRequest;
import ru.itmo.love.dto.hotel.RoomDTO;
import ru.itmo.love.entity.Room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

 // контракт сервиса отелей и комнат
public interface HotelServiceInt {

 // ищет отели по фильтрам
    List<HotelDTO> searchHotels(HotelSearchRequest request);

 // возвращает свободные комнаты отеля
    List<RoomDTO> getAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut);

 // возвращает отель по id
    HotelDTO getHotelById(Long hotelId);

 // создает отель
    HotelDTO createHotel(HotelDTO hotelDTO);

 // возвращает все отели
    List<HotelDTO> getAllHotels();

 // возвращает все комнаты
    List<RoomDTO> getAllRooms();

 // возвращает сущность комнаты по id
    Room getRoomEntityById(Long roomId);

 // меняет доступность комнаты
    void setRoomAvailability(Long roomId, boolean available);

    /** возвращает идентификаторы всех отелей для пакетного пересчета рейтинга */
    List<Long> getAllHotelIds();

    /** обновляет агрегированный рейтинг отеля по результатам обработки рецензий */
    void updateReviewRating(Long hotelId, BigDecimal averageRating, int totalReviews);
}
