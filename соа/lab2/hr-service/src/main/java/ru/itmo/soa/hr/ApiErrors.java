package ru.itmo.soa.hr;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class ApiErrors extends ResponseEntityExceptionHandler {
    public record Error(int status, String message) {}

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus known = HttpStatus.resolve(status.value());
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.putAll(headers);
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        return super.handleExceptionInternal(exception,
                new Error(status.value(), known == null ? "Request failed" : known.getReasonPhrase()),
                jsonHeaders, status, request);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Error> handle(ApiException exception) {
        return ResponseEntity.status(exception.status()).contentType(MediaType.APPLICATION_JSON)
                .body(new Error(exception.status(), exception.getMessage()));
    }
}
