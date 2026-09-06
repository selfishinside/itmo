package ru.itmo.love.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 // запрос фильтрации отелей по городу названию рейтингу цене и вместимости
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSearchRequest {
    private String city;
    private String hotelName;
    private Integer minRating;
    private Double maxPrice;
    private Long numberOfGuests;
}

