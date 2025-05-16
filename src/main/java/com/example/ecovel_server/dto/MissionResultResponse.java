package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionResultResponse {
    private int day;
    private String placeId;
    private String result;
    private String imageUrl; // upload image

    public String getDay() {
        return "DAY " + day;
    }
}