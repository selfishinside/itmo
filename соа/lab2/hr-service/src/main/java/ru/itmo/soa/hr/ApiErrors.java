package ru.itmo.soa.hr;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class ApiErrors {
    public record Error(int status, String message) {}

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Error> handle(ApiException exception) {
        return ResponseEntity.status(exception.status()).body(new Error(exception.status(), exception.getMessage()));
    }
}
