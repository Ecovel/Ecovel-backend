package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GrowthLogResponseDto {
    private String growthStage;              // 성장 단계 (예: 씨앗, 새싹)
    private int totalQuizSuccessCount;       // 퀴즈 성공 횟수
    private int totalMissionSuccessCount;    // 미션 성공 횟수
    private double totalCarbonSaved;         // 누적 탄소 절감량 (g)
}
