package ru.itmo.love.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// чек бронирования для гостя
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingReceiptDTO {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime purchaseDate;
    private String guestName;
    private String hotelName;
    private String roomNumber;
}