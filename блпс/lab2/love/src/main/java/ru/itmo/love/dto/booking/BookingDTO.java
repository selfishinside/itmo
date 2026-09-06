package ru.itmo.love.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.love.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

// данные бронирования, возвращаемые клиенту через api
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long id;
    private Long roomId;
    private Long guestId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private String voucherPath;
}

