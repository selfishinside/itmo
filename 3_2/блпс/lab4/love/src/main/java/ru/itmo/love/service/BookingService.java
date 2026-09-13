package ru.itmo.love.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.love.bpm.WorkflowGateway;
import ru.itmo.love.dto.booking.*;
import ru.itmo.love.entity.Booking;
import ru.itmo.love.entity.enums.BookingStatus;
import java.time.LocalDate;
import java.util.*;

/** Compatibility REST facade: every command advances the BPMN process. */
@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class BookingService implements BookingServiceInt {
    private final BookingActivitiesInt activities;
    private final WorkflowGateway workflow;

    public BookingDTO createBooking(CreateBookingRequest r) {
        Map<String,Object> values = new HashMap<>();
        values.put("roomId", r.getRoomId()); values.put("guestId", r.getGuestId());
        values.put("checkInDate", r.getCheckInDate().toString());
        values.put("checkOutDate", r.getCheckOutDate().toString());
        values.put("numberOfGuests", r.getNumberOfGuests().longValue());
        String card = r.getCardNumber();
        values.put("cardNumber", card == null || card.length() < 4 ? "****" : "**** " + card.substring(card.length()-4));
        values.put("cardHolder", r.getCardHolder());
        var process = workflow.start("hotelBooking", values);
        Number bookingId = (Number) workflow.waitVariable(process.id(), "bookingId");
        return activities.getBookingById(bookingId.longValue());
    }
    public BookingDTO confirmPayment(Long id) {
        workflow.completeBooking(id,"payment",Map.of());
        return waitStatus(id, BookingStatus.PAID);
    }
    public BookingDTO confirmBookingByHotel(Long id) {
        workflow.completeBooking(id,"hotelConfirmation",Map.of());
        return waitStatus(id, BookingStatus.CONFIRMED);
    }
    public BookingDTO cancelBooking(Long id,String reason) {
        workflow.completeBooking(id,"cancellationRequest",Map.of("reason",reason));
        return waitStatus(id, BookingStatus.CANCELLATION_REQUESTED);
    }
    public BookingDTO cancelBookingByHotel(Long id,Boolean approved,String reason) {
        workflow.completeBooking(id,"cancellationDecision",Map.of("approved",Objects.requireNonNull(approved),"reason",reason));
        return waitStatus(id, approved ? BookingStatus.CANCELLED : BookingStatus.CONFIRMED);
    }
    public void handlePaymentTimeout() { workflow.start("paymentSweep", Map.of()); }
    public List<BookingDTO> getBookingsByGuest(Long id) { return activities.getBookingsByGuest(id); }
    public List<BookingDTO> getBookingsByHotel(Long id) { return activities.getBookingsByHotel(id); }
    public BookingDTO getBookingById(Long id) { return activities.getBookingById(id); }
    public BookingReceiptDTO getBookingReceipt(Long id) { return activities.getBookingReceipt(id); }
    public List<BookingDTO> getAllBookings() { return activities.getAllBookings(); }
    public Booking getBookingEntityById(Long id) { return activities.getBookingEntityById(id); }
    public List<Booking> getConfirmedBookingsForCheckIn(LocalDate date) { return activities.getConfirmedBookingsForCheckIn(date); }

    private BookingDTO waitStatus(Long id, BookingStatus status) {
        long deadline = System.nanoTime() + java.time.Duration.ofSeconds(10).toNanos();
        BookingDTO booking = activities.getBookingById(id);
        while (booking.getStatus() != status && System.nanoTime() < deadline) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting booking status", e);
            }
            booking = activities.getBookingById(id);
        }
        return booking;
    }
}
