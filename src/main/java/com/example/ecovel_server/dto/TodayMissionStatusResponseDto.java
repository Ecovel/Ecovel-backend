package com.example.ecovel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TodayMissionStatusResponseDto {
    private int day;       // What day is it today
    private boolean isCompleted; // Did you authenticate today

    public String getDay() {
        return "DAY " + day;
    }
}