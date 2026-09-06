package ru.itmo.love.service;

import ru.itmo.love.dto.booking.BookingDTO;
import ru.itmo.love.dto.booking.BookingReceiptDTO;
import ru.itmo.love.dto.booking.CreateBookingRequest;

import java.util.List;

// контракт сервиса бронирований
public interface BookingServiceInt {

    // создает бронь
    BookingDTO createBooking(CreateBookingRequest request);

    // подтверждает оплату
    BookingDTO confirmPayment(Long bookingId);

    // подтверждает бронь отелем
    BookingDTO confirmBookingByHotel(Long bookingId);

    // создает запрос на отмену
    BookingDTO cancelBooking(Long bookingId, String reason);

    // обрабатывает решение отеля по отмене
    BookingDTO cancelBookingByHotel(Long bookingId, Boolean approved, String reason);

    // закрывает просроченные оплаты
    void handlePaymentTimeout();

    // возвращает брони гостя
    List<BookingDTO> getBookingsByGuest(Long guestId);

    // возвращает брони отеля
    List<BookingDTO> getBookingsByHotel(Long hotelId);

    // возвращает бронь по id
    BookingDTO getBookingById(Long bookingId);

    // формирует чек брони
    BookingReceiptDTO getBookingReceipt(Long bookingId);

    // возвращает все брони
    List<BookingDTO> getAllBookings();
}
