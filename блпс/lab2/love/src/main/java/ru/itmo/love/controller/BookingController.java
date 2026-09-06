package ru.itmo.love.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.love.dto.common.ApiResponse;
import ru.itmo.love.dto.booking.BookingDTO;
import ru.itmo.love.dto.booking.BookingReceiptDTO;
import ru.itmo.love.dto.booking.CancellationRequest;
import ru.itmo.love.dto.booking.CreateBookingRequest;
import ru.itmo.love.service.BookingServiceInt;

import java.util.List;

/**
 * контроллер для управления бронированиями
 * обрабатывает создание оплату отмену и получение чеков
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingServiceInt bookingService;

    /**
     * создает новое бронирование
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_CREATE)")
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(@RequestBody CreateBookingRequest request) {
        log.info("Create booking request for guest {} and room {}", request.getGuestId(), request.getRoomId());
        try {
            BookingDTO booking = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(booking, "Booking created successfully. Waiting for payment."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "INVALID_REQUEST"));
        } catch (Exception e) {
            log.error("Error creating booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "CREATE_ERROR"));
        }
    }

    /**
     * подтверждает оплату бронирования
     */
    @PostMapping("/{bookingId}/confirm-payment")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_CONFIRM_PAYMENT)")
    public ResponseEntity<ApiResponse<BookingDTO>> confirmPayment(@PathVariable Long bookingId) {
        log.info("Confirm payment for booking {}", bookingId);
        try {
            BookingDTO booking = bookingService.confirmPayment(bookingId);
            return ResponseEntity.ok(ApiResponse.success(booking, "Payment confirmed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
        } catch (Exception e) {
            log.error("Error confirming payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "CONFIRM_ERROR"));
        }
    }

    /**
     * подтверждает бронирование отелем
     */
    @PostMapping("/{bookingId}/confirm-hotel")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_CONFIRM_HOTEL)")
    public ResponseEntity<ApiResponse<BookingDTO>> confirmByHotel(@PathVariable Long bookingId) {
        log.info("Hotel confirmation for booking {}", bookingId);
        try {
            BookingDTO booking = bookingService.confirmBookingByHotel(bookingId);
            return ResponseEntity.ok(ApiResponse.success(booking, "Booking confirmed by hotel. Voucher generated."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
        } catch (Exception e) {
            log.error("Error confirming booking by hotel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "CONFIRM_ERROR"));
        }
    }

    /**
     * создает запрос на отмену бронирования
     */
    @PostMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_REQUEST_CANCELLATION)")
    public ResponseEntity<ApiResponse<BookingDTO>> requestCancellation(
            @PathVariable Long bookingId,
            @RequestBody CancellationRequest request) {
        log.info("Request cancellation for booking {}", bookingId);
        try {
            BookingDTO booking = bookingService.cancelBooking(bookingId, request.getReason());
            return ResponseEntity.ok(ApiResponse.success(booking, "Cancellation request created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
        } catch (Exception e) {
            log.error("Error requesting cancellation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "CANCEL_ERROR"));
        }
    }

    /**
     * обрабатывает решение отеля по запросу отмены
     */
    @PostMapping("/{bookingId}/cancel-hotel")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_RESOLVE_CANCELLATION)")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelByHotel(
            @PathVariable Long bookingId,
            @RequestBody CancellationRequest request) {
        log.info("Hotel cancellation request for booking {}", bookingId);
        try {
            BookingDTO booking = bookingService.cancelBookingByHotel(bookingId, request.getApproved(), request.getReason());
            return ResponseEntity.ok(ApiResponse.success(booking, "Booking cancellation response processed by hotel"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
        } catch (Exception e) {
            log.error("Error cancelling booking by hotel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "CANCEL_ERROR"));
        }
    }

    /**
     * возвращает одно бронирование по id
     */
    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_READ)")
    public ResponseEntity<ApiResponse<BookingDTO>> getBooking(@PathVariable Long bookingId) {
        log.info("Get booking request for id {}", bookingId);
        try {
            BookingDTO booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(ApiResponse.success(booking, "Booking found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
        } catch (Exception e) {
            log.error("Error getting booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_ERROR"));
        }
    }

    /**
     * возвращает бронирования гостя
     */
    @GetMapping("/guest/{guestId}")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_READ)")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getGuestBookings(@PathVariable Long guestId) {
        log.info("Get bookings for guest {}", guestId);
        try {
            List<BookingDTO> bookings = bookingService.getBookingsByGuest(guestId);
            return ResponseEntity.ok(ApiResponse.success(bookings, "Guest bookings found: " + bookings.size()));
        } catch (Exception e) {
            log.error("Error getting guest bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_ERROR"));
        }
    }

    /**
     * возвращает бронирования отеля
     */
    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_READ)")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getHotelBookings(@PathVariable Long hotelId) {
        log.info("Get bookings for hotel {}", hotelId);
        try {
            List<BookingDTO> bookings = bookingService.getBookingsByHotel(hotelId);
            return ResponseEntity.ok(ApiResponse.success(bookings, "Hotel bookings found: " + bookings.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting hotel bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_ERROR"));
        }
    }

    /**
     * запускает проверку просроченной оплаты
     */
    @PostMapping("/handle-timeout")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_HANDLE_TIMEOUT)")
    public ResponseEntity<ApiResponse<String>> handlePaymentTimeout() {
        log.info("Handle payment timeout");
        try {
            bookingService.handlePaymentTimeout();
            return ResponseEntity.ok(ApiResponse.success("OK", "Payment timeout handled"));
        } catch (Exception e) {
            log.error("Error handling payment timeout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "TIMEOUT_ERROR"));
        }
    }

    /**
     * возвращает чек для подтвержденного бронирования
     */
    @GetMapping("/{bookingId}/receipt")
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_RECEIPT_READ)")
    public ResponseEntity<ApiResponse<BookingReceiptDTO>> getBookingReceipt(@PathVariable Long bookingId) {
        log.info("Get receipt request for booking {}", bookingId);
        try {
            BookingReceiptDTO receipt = bookingService.getBookingReceipt(bookingId);
            return ResponseEntity.ok(ApiResponse.success(receipt, "Receipt retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), "NOT_FOUND"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
        } catch (Exception e) {
            log.error("Error getting receipt", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_ERROR"));
        }
    }

    /**
     * возвращает список всех бронирований
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(ru.itmo.love.security.policy.Privilege).BOOKING_READ)")
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings() {
        log.info("Get all bookings request");
        try {
            List<BookingDTO> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(ApiResponse.success(bookings, "Bookings found: " + bookings.size()));
        } catch (Exception e) {
            log.error("Error getting all bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "INTERNAL_ERROR"));
        }
    }
}
