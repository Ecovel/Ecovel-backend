package com.example.ecovel_server.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelScheduleDto {
    private int day;                              // "DAY 1"
    private List<TravelPlaceDto> places;             // 장소 리스트

    // 프론트 응답용: "DAY N" 포맷으로 보여주기
    public String getDay() {
        return "DAY " + day;
    }
}