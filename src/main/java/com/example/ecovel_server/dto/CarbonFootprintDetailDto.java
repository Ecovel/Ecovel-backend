package com.example.ecovel_server.dto;

import lombok.*;

import java.util.List;

//일자별 탄소 배출량 및 절감량

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonFootprintDetailDto {
    private int day;
    private String transportMode;
    private Double vehicleCarbon;
    private Double actualCarbon;

    // 프론트 응답용: "DAY N" 포맷으로 보여주기
    public String getDay() {
        return "DAY " + day;
    }
}