package com.example.ecovel_server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSubmitRequestDto {
    private Long userId;
    private boolean answer; // true(O), false(X)
}

