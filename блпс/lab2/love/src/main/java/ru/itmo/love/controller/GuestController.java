package ru.itmo.love.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.love.dto.common.ApiResponse;
import ru.itmo.love.dto.guest.CreateGuestRequest;
import ru.itmo.love.entity.Guest;
import ru.itmo.love.service.GuestServiceInt;
import java.util.List;

/**
 * контроллер для работы с гостями
 * обрабатывает создание поиск и вывод списка гостей
 */
@RestController
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
@Slf4j
public class GuestController {

    private final GuestServiceInt guestService;

    /**
     * создает нового гостя
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).GUEST_CREATE)")
    public ResponseEntity<ApiResponse<Guest>> createGuest(@RequestBody CreateGuestRequest request) {
        log.info("Create guest request for {}", request.getEmail());
        try {
            Guest guest = guestService.createGuest(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(guest, "Guest created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        } catch (Exception e) {
            log.error("Error creating guest", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "CREATE_ERROR"));
        }
    }

    /**
     * возвращает гостя по id
     */
    @GetMapping("/{guestId}")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).GUEST_READ)")
    public ResponseEntity<ApiResponse<Guest>> getGuest(@PathVariable Long guestId) {
        log.info("Get guest request for id {}", guestId);
        try {
            Guest guest = guestService.getGuestById(guestId);
            return ResponseEntity.ok(ApiResponse.success(guest, "Guest found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting guest", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_ERROR"));
        }
    }

    /**
     * возвращает гостя по email
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).GUEST_READ)")
    public ResponseEntity<ApiResponse<Guest>> getGuestByEmail(@PathVariable String email) {
        log.info("Get guest request by email: {}", email);
        try {
            Guest guest = guestService.getGuestByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(guest, "Guest found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting guest by email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_ERROR"));
        }
    }

    /**
     * возвращает всех гостей
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).GUEST_READ)")
    public ResponseEntity<ApiResponse<List<Guest>>> getAllGuests() {
        log.info("Get all guests request");
        try {
            List<Guest> guests = guestService.getAllGuests();
            return ResponseEntity.ok(ApiResponse.success(guests, "Guests found: " + guests.size()));
        } catch (Exception e) {
            log.error("Error getting all guests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "INTERNAL_ERROR"));
        }
    }
}
