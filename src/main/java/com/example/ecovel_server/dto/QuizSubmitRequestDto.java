package com.example.ecovel_server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSubmitRequestDto {
    private Long userId;
    private boolean answer; // 사용자의 응답: true(O), false(X)
}

