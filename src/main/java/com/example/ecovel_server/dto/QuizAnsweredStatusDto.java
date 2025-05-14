package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuizAnsweredStatusDto {
    private boolean answered; // 오늘 퀴즈를 풀었는지 여부
}
