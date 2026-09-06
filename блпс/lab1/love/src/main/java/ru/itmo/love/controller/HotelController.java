package ru.itmo.love.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.love.dto.ApiResponse;
import ru.itmo.love.dto.HotelDTO;
import ru.itmo.love.dto.HotelSearchRequest;
import ru.itmo.love.dto.RoomDTO;
import ru.itmo.love.service.HotelServiceInt;

import java.time.LocalDate;
import java.util.List;

/**
 * контроллер для управления отелями и комнатами
 * обрабатывает поиск получение доступных комнат и создание отелей
 */
@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelServiceInt hotelService;

    /**
     * возвращает все отели
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HotelDTO>>> getAllHotels() {
        log.info("Get all hotels request");
        try {
            List<HotelDTO> hotels = hotelService.getAllHotels();
            return ResponseEntity.ok(ApiResponse.success(hotels, "Hotels found: " + hotels.size()));
        } catch (Exception e) {
            log.error("Error getting all hotels", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "INTERNAL_ERROR"));
        }
    }

    /**
     * ищет отели по фильтрам
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<HotelDTO>>> searchHotels(@RequestBody HotelSearchRequest request) {
        log.info("Search hotels request received");
        try {
            List<HotelDTO> hotels = hotelService.searchHotels(request);
            return ResponseEntity.ok(ApiResponse.success(hotels, "Hotels found: " + hotels.size()));
        } catch (Exception e) {
            log.error("Error searching hotels", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "SEARCH_ERROR"));
        }
    }

    /**
     * возвращает отель по id
     */
    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelDTO>> getHotel(@PathVariable Long hotelId) {
        log.info("Get hotel request for id: {}", hotelId);
        try {
            HotelDTO hotel = hotelService.getHotelById(hotelId);
            return ResponseEntity.ok(ApiResponse.success(hotel, "Hotel found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "HOTEL_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting hotel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "INTERNAL_ERROR"));
        }
    }

    /**
     * возвращает доступные комнаты по дате заезда и выезда
     */
    @GetMapping("/{hotelId}/available-rooms")
    public ResponseEntity<ApiResponse<List<RoomDTO>>> getAvailableRooms(
            @PathVariable Long hotelId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut) {
        log.info("Get available rooms for hotel {} from {} to {}", hotelId, checkIn, checkOut);
        try {
            List<RoomDTO> rooms = hotelService.getAvailableRooms(hotelId, checkIn, checkOut);
            return ResponseEntity.ok(ApiResponse.success(rooms, "Available rooms found: " + rooms.size()));
        } catch (Exception e) {
            log.error("Error getting available rooms", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "ROOMS_ERROR"));
        }
    }

    /**
     * создает новый отель
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HotelDTO>> createHotel(@RequestBody HotelDTO hotelDTO) {
        log.info("Create hotel request: {}", hotelDTO.getName());
        try {
            HotelDTO created = hotelService.createHotel(hotelDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(created, "Hotel created successfully"));
        } catch (Exception e) {
            log.error("Error creating hotel", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "CREATE_ERROR"));
        }
    }
}
