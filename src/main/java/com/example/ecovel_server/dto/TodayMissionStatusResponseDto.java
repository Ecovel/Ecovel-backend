package com.example.ecovel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TodayMissionStatusResponseDto {
    private int day;       // 오늘이 몇일차인지
    private boolean isCompleted; // 오늘 인증 했는지 여부

    // 프론트 응답용: "DAY N" 포맷으로 보여주기
    public String getDay() {
        return "DAY " + day;
    }
}