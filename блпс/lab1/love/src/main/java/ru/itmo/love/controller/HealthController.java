package ru.itmo.love.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.love.dto.ApiResponse;

// отдает служебные статусы приложения
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthController {

    // проверка что приложение доступно
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("UP", "Application is running"));
    }

    // возвращает краткую информацию о сервисе
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<String>> info() {
        return ResponseEntity.ok(ApiResponse.success("Hotel Booking API v1.0", "Love Hotel Booking System"));
    }
}
