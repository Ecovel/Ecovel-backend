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

        private String imageUrl;

        private Integer walkTime;
        private Integer bicycleTime;
        private Integer publicTime;
        private Integer carTime;

        private Double latitude;
        private Double longitude;
    }

    @Getter
    @Setter
    public static class TravelAIDay {
        private String day;                     // "DAY 1"
        private List<TravelAIPlace> places;     // places on that date
    }

    private List<TravelAIDay> scheduleList;     // Full schedule by DAY
}