package com.example.ecovel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TodayMissionContentResponseDto {
    private int day;
    private String placeName;
    private String description;

    public String getDay() {
        return "DAY " + day;
    }
}