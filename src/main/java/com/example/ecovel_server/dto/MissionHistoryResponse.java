package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionHistoryResponse {
    private int day;
    private String placeId;
    private String result;
    private String verifiedAt; // ISO format or yyyy-MM-dd HH:mm

    public String getDay() {
        return "DAY " + day;
    }
}