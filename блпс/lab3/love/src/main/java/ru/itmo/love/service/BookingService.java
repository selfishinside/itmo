package ru.itmo.love.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.itmo.love.dto.booking.BookingDTO;
import ru.itmo.love.dto.booking.BookingReceiptDTO;
import ru.itmo.love.dto.booking.CreateBookingRequest;
import ru.itmo.love.entity.Booking;
import ru.itmo.love.entity.enums.BookingStatus;
import ru.itmo.love.entity.Room;
import ru.itmo.love.entity.Guest;
import ru.itmo.love.event.BookingConfirmedEvent;
import ru.itmo.love.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * сервис для управления бронированиями
 * содержит бизнес логику бронирования оплат отмен и чеков
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService implements BookingServiceInt {

    private final BookingRepository bookingRepository;
    private final AuditServiceInt auditService;
    private final GuestServiceInt guestService;
    private final HotelServiceInt hotelService;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisherInt eventPublisher;

    private static final int PAYMENT_TIMEOUT_MINUTES = 1; // таймаут оплаты в минутах
    private static final double DEPOSIT_PERCENTAGE = 0.10; // доля предоплаты

    /**
     * создает новое бронирование
 */
    @Override
    public BookingDTO createBooking(CreateBookingRequest request) {
        log.info("Creating booking for guest {} and room {}", request.getGuestId(), request.getRoomId());
        return executeInTransaction(() -> {
            Room room = hotelService.getRoomEntityById(request.getRoomId());
            Guest guest = guestService.getGuestById(request.getGuestId());

            if (!room.getAvailable()) {
                throw new IllegalArgumentException("Room is not available");
            }

            List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.PENDING_PAYMENT, BookingStatus.PAID, BookingStatus.CONFIRMED);
            List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                    request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate(), activeStatuses);
            if (!conflictingBookings.isEmpty()) {
                throw new IllegalArgumentException("Room is already booked for the selected dates");
            }

            long numberOfNights = java.time.temporal.ChronoUnit.DAYS.between(
                    request.getCheckInDate(), request.getCheckOutDate()
            );
            double totalPrice = room.getPricePerNight() * numberOfNights;
            double depositAmount = totalPrice * DEPOSIT_PERCENTAGE;

            Booking booking = Booking.builder()
                    .room(room)
                    .guest(guest)
                    .checkInDate(request.getCheckInDate())
                    .checkOutDate(request.getCheckOutDate())
                    .numberOfGuests(request.getNumberOfGuests())
                    .totalPrice(totalPrice)
                    .cardNumber(maskCardNumber(request.getCardNumber()))
                    .cardHolder(request.getCardHolder())
                    .depositAmount(depositAmount)
                    .status(BookingStatus.PENDING_PAYMENT)
                    .createdAt(LocalDateTime.now())
                    .build();

            booking = bookingRepository.save(booking);
            syncRoomAvailability(room.getId());

            auditService.logAction("CREATE_BOOKING", "BOOKING", booking.getId(), "SUCCESS",
                    "Booking created with status PENDING_PAYMENT");

            log.info("Booking created successfully with id {}", booking.getId());
            return convertToDTO(booking);
        });
    }

    /**
     * подтверждает оплату бронирования
 */
    @Override
    public BookingDTO confirmPayment(Long bookingId) {
        log.info("Confirming payment for booking {}", bookingId);
        return executeInTransaction(() -> {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            if (!booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)) {
                throw new IllegalStateException("Booking is not in PENDING_PAYMENT status");
            }

 // подтверждает оплату
            booking.setStatus(BookingStatus.PAID);
            booking = bookingRepository.save(booking);

            auditService.logAction("CONFIRM_PAYMENT", "BOOKING", booking.getId(), "SUCCESS",
                    "Payment confirmed");

            log.info("Payment confirmed for booking {}", bookingId);
            return convertToDTO(booking);
        });
    }

    /**
     * подтверждает бронирование отелем
 */
    @Override
    public BookingDTO confirmBookingByHotel(Long bookingId) {
        log.info("Confirming booking by hotel for booking {}", bookingId);
        return executeInTransaction(() -> {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            if (!booking.getStatus().equals(BookingStatus.PAID)) {
                throw new IllegalStateException("Booking must be in PAID status before hotel confirmation");
            }

            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setConfirmedAt(LocalDateTime.now());
            booking = bookingRepository.save(booking);

            auditService.logAction("CONFIRM_BOOKING_HOTEL", "BOOKING", booking.getId(), "SUCCESS",
                    "Booking confirmed by hotel");

            eventPublisher.publishBookingConfirmed(new BookingConfirmedEvent(
                    UUID.randomUUID().toString(),
                    booking.getId(),
                    booking.getGuest().getId(),
                    booking.getGuest().getEmail(),
                    booking.getRoom().getHotel().getName(),
                    LocalDateTime.now()
            ));

            log.info("Booking confirmed by hotel: {}", bookingId);
            return convertToDTO(booking);
        });
    }

    /**
     * создает запрос на отмену бронирования
 */
    @Override
    public BookingDTO cancelBooking(Long bookingId, String reason) {
        log.info("Requesting cancellation for booking {}", bookingId);
        return executeInTransaction(() -> {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            if (!booking.getStatus().equals(BookingStatus.CONFIRMED)) {
                throw new IllegalStateException("Cancellation request can only be created for confirmed bookings");
            }

            if (booking.getCheckInDate().isBefore(java.time.LocalDate.now().plusDays(1))) {
                throw new IllegalStateException("Cancellation request must be made at least one day before check-in");
            }

            if (booking.getStatus().equals(BookingStatus.CANCELLATION_REQUESTED)) {
                throw new IllegalStateException("Cancellation request is already pending for this booking");
            }

            booking.setStatus(BookingStatus.CANCELLATION_REQUESTED);
            booking.setCancellationReason(reason);
            booking = bookingRepository.save(booking);

            auditService.logAction("REQUEST_CANCELLATION", "BOOKING", booking.getId(), "PENDING",
                    "Cancellation requested: " + reason);

            log.info("Cancellation request created for booking {}", bookingId);
            return convertToDTO(booking);
        });
    }

    /**
     * обрабатывает ответ отеля на запрос отмены
 */
    @Override
    public BookingDTO cancelBookingByHotel(Long bookingId, Boolean approved, String reason) {
        log.info("Processing hotel cancellation response for booking {}", bookingId);
        return executeInTransaction(() -> {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            if (!booking.getStatus().equals(BookingStatus.CANCELLATION_REQUESTED)) {
                throw new IllegalStateException("No cancellation request pending for this booking");
            }

            if (Boolean.TRUE.equals(approved)) {
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setCancelledAt(LocalDateTime.now());
                booking = bookingRepository.save(booking);
                syncRoomAvailability(booking.getRoom().getId());
                auditService.logAction("CANCEL_BOOKING_HOTEL", "BOOKING", booking.getId(), "SUCCESS",
                        "Cancellation approved by hotel: " + reason);
                log.info("Booking cancelled by hotel successfully: {}", bookingId);
            } else {
                booking.setStatus(BookingStatus.CONFIRMED);
                auditService.logAction("CANCEL_BOOKING_HOTEL", "BOOKING", booking.getId(), "DENIED",
                        "Cancellation denied by hotel: " + reason);
                log.info("Cancellation request denied for booking {}", bookingId);
            }

            booking.setCancellationReason(reason);
            booking = bookingRepository.save(booking);
            syncRoomAvailability(booking.getRoom().getId());
            return convertToDTO(booking);
        });
    }

    /**
     * обрабатывает просроченные платежи
 */
    @Scheduled(fixedRate = 60000) // каждую минуту
    @Override
    public void handlePaymentTimeout() {
        log.info("Handling payment timeout");
        executeInTransactionWithoutResult(() -> {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES);
            List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore(
                    BookingStatus.PENDING_PAYMENT, cutoffTime
            );

            for (Booking booking : expiredBookings) {
                booking.setStatus(BookingStatus.PAYMENT_TIMEOUT);
                bookingRepository.save(booking);
                syncRoomAvailability(booking.getRoom().getId());
                auditService.logAction("PAYMENT_TIMEOUT", "BOOKING", booking.getId(), "SUCCESS",
                        "Payment timeout for booking");
                log.info("Booking {} marked as payment timeout", booking.getId());
            }
        });
    }

    /**
     * возвращает бронирования гостя
 */
    @Override
    public List<BookingDTO> getBookingsByGuest(Long guestId) {
        return bookingRepository.findByGuestId(guestId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * возвращает бронирования отеля
 */
    @Override
    public List<BookingDTO> getBookingsByHotel(Long hotelId) {
        hotelService.getHotelById(hotelId);
        return bookingRepository.findByRoomHotelId(hotelId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * возвращает бронирование по id
 */
    @Override
    public BookingDTO getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    private BookingDTO convertToDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .roomId(booking.getRoom().getId())
                .guestId(booking.getGuest().getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numberOfGuests(booking.getNumberOfGuests())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .confirmedAt(booking.getConfirmedAt())
                .voucherPath(booking.getVoucherPath())
                .build();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private void syncRoomAvailability(Long roomId) {
        List<BookingStatus> activeStatuses = Arrays.asList(
                BookingStatus.PENDING_PAYMENT,
                BookingStatus.PAID,
                BookingStatus.CONFIRMED,
                BookingStatus.CANCELLATION_REQUESTED
        );

        boolean hasActiveBookings = bookingRepository.findByRoomId(roomId).stream()
                .anyMatch(booking -> activeStatuses.contains(booking.getStatus()));

        hotelService.setRoomAvailability(roomId, !hasActiveBookings);
    }

    private <T> T executeInTransaction(java.util.function.Supplier<T> action) {
        try {
            return transactionTemplate.execute(status -> action.get());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Transactional booking operation failed", e);
            throw new RuntimeException("Transactional booking operation failed: " + e.getMessage(), e);
        }
    }

    private void executeInTransactionWithoutResult(Runnable action) {
        executeInTransaction(() -> {
            action.run();
            return null;
        });
    }

    /**
     * возвращает чек для подтвержденного бронирования
 */
    @Override
    public BookingReceiptDTO getBookingReceipt(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            String message = switch (booking.getStatus()) {
                case PENDING_PAYMENT -> "Receipt is not available: booking is waiting for payment";
                case PAID -> "Receipt is not available: booking is paid but not confirmed by hotel";
                case CANCELLATION_REQUESTED -> "Receipt is not available: cancellation request is pending";
                case CANCELLED -> "Receipt is not available: booking has been cancelled";
                case PAYMENT_TIMEOUT -> "Receipt is not available: payment timeout occurred";
                case COMPLETED -> "Receipt is not available: booking has been completed";
                default -> "Receipt is not available for this booking status";
            };
            throw new IllegalStateException(message);
        }

        return BookingReceiptDTO.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .purchaseDate(booking.getCreatedAt())
                .guestName(booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName())
                .hotelName(booking.getRoom().getHotel().getName())
                .roomNumber(booking.getRoom().getRoomNumber())
                .build();
    }

    @Transactional(readOnly = true)
    /**
     * возвращает все бронирования
 */
    @Override
    public List<BookingDTO> getAllBookings() {
        log.info("Getting all bookings");
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /** возвращает сущность бронирования для других бизнес сервисов без прямого доступа к репозиторию */
    @Override
    @Transactional(readOnly = true)
    public Booking getBookingEntityById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    /** возвращает подтвержденные бронирования для которых нужно создать напоминание */
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getConfirmedBookingsForCheckIn(java.time.LocalDate checkInDate) {
        return bookingRepository.findByStatusAndCheckInDate(BookingStatus.CONFIRMED, checkInDate);
    }
}
