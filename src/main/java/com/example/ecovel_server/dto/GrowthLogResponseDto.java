package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GrowthLogResponseDto {
    private String growthStage;              // Growth stage (e.g., seeds, sprouts)
    private int totalQuizSuccessCount;       // Number of successful quiz
    private int totalMissionSuccessCount;    // Number of successful missions
    private double totalCarbonSaved;         // Accumulated carbon savings (g)
}
