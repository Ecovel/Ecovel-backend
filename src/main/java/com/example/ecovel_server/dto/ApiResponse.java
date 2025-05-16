package com.example.ecovel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private HttpStatus httpStatus;
    private boolean success;
    private T result;
    private String error;

    //success
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(HttpStatus.OK, true, result, null);
    }

    //error
    public static <T> ApiResponse<T> error(String errorMessage) {
        return new ApiResponse<>(HttpStatus.OK, false, null, errorMessage);
    }
}