package ru.itmo.love.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// данные комнаты
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private String roomType;
    private Integer capacity;
    private Double pricePerNight;
    private String description;
    private Boolean available;
}
