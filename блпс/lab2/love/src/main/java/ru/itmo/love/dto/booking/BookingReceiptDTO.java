package ru.itmo.love.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// чек бронирования с информацией о госте, отеле и датах проживания
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

