package ru.itmo.love.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

 // данные отеля для отображения клиенту включая список номеров
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDTO {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private Integer rating;
    /** средняя оценка рассчитанная по опубликованным рецензиям */
    private BigDecimal averageReviewRating;
    /** количество опубликованных рецензий участвующих в рейтинге */
    private Integer totalReviews;
    private String description;
    private Double latitude;
    private Double longitude;
    private List<RoomDTO> rooms;
}
