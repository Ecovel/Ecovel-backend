package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionResultResponse {
    private int day;
    private String placeId;
    private boolean verified; //성공 여부
    private boolean faceMatch;   // 추가
    private boolean placeMatch;  // 추가
    private String message;
    private String imageUrl;

    // 프론트 응답용: "DAY N" 포맷으로 보여주기
    public String getDay() {
        return "DAY " + day;
    }
}