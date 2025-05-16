package com.example.ecovel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MissionLocationResponseDto {
    private int day;
    private String placeName;
    private Double latitude;
    private Double longitude;

    public String getDay() {
        return "DAY " + day;
    }
}
