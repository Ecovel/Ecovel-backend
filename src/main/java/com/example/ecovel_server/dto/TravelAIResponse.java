package com.example.ecovel_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TravelAIResponse {

    @Getter
    @Setter
    public static class TravelAIPlace {
        private String name;

        private Integer walkTime;
        private Integer bicycleTime;
        private Integer publicTime;
        private Integer carTime;

        private Double latitude;  // 위도 추가
        private Double longitude; // 경도 추가
    }

    @Getter
    @Setter
    public static class TravelAIDay {
        private String day;                     // "DAY 1"
        private List<TravelAIPlace> places;     // 해당 날짜의 장소들
    }

    private List<TravelAIDay> scheduleList;     // DAY별 일정 전체
}