package ru.itmo.love.service;

import ru.itmo.love.dto.HotelDTO;
import ru.itmo.love.dto.HotelSearchRequest;
import ru.itmo.love.dto.RoomDTO;
import ru.itmo.love.entity.Room;

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
}
