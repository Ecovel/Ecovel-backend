package com.example.ecovel_server.exception;

//spring에서 모든 controller에 적용되는 전역 예외 처리기

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(errorResponse("BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {
        return ResponseEntity.internalServerError().body(errorResponse("SERVER_ERROR", e.getMessage()));
    }

    private Map<String, Object> errorResponse(String code, String message) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "code", code,
                "message", message
        );
    }
}