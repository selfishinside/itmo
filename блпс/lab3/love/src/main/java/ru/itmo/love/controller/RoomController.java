package ru.itmo.love.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.love.dto.common.ApiResponse;
import ru.itmo.love.dto.hotel.RoomDTO;
import ru.itmo.love.service.HotelServiceInt;

import java.util.List;

/**
 * контроллер для получения информации о комнатах
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final HotelServiceInt hotelService;

    /**
     * возвращает все комнаты
 */
    @GetMapping
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).ROOM_READ)")
    public ResponseEntity<ApiResponse<List<RoomDTO>>> getAllRooms() {
        log.info("Get all rooms request");
        try {
            List<RoomDTO> rooms = hotelService.getAllRooms();
            return ResponseEntity.ok(ApiResponse.success(rooms, "Rooms found: " + rooms.size()));
        } catch (Exception e) {
            log.error("Error getting all rooms", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "INTERNAL_ERROR"));
        }
    }
}