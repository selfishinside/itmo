package ru.itmo.love.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 // запрос на отмену бронирования причина и решение одобрить отклонить
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationRequest {
    private String reason;
    private Boolean approved;
}

