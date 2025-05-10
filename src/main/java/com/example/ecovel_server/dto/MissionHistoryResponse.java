package com.example.ecovel_server.dto;

//여러 건을 반환할 때 리스트로 사용 가능 (일단 이거는 사용할지 안할지 모름)

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionHistoryResponse {
    private int day;
    private String placeId;
    private boolean verified;
    private boolean faceMatch;   // 추가
    private boolean placeMatch;  // 추가
    private String imageUrl;
    private String message;
    private String verifiedAt; // ISO format 또는 yyyy-MM-dd HH:mm

    // 프론트 응답용: "DAY N" 포맷으로 보여주기
    public String getDay() {
        return "DAY " + day;
    }
}