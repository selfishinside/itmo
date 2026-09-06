package ru.itmo.love.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// из клиента запрос поиска отелей
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
