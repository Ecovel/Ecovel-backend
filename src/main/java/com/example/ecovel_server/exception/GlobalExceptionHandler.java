package com.example.ecovel_server.exception;

//Global exception handler applied to all controllers in spring

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