package ru.itmo.love.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// запрос на отмену с причиной и решением
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationRequest {
    private String reason;
    private Boolean approved;
}
