package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DailyQuizDto {
    private String question; // 퀴즈 질문
    private String date;     // 날짜 (예: "2025-05-13")
    private boolean answer;      // 정답 (true = O, false = X)
    private String explanation;  // 정답 해설
}

