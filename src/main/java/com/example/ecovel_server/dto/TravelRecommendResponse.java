package com.example.ecovel_server.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelRecommendResponse {
    private Long planId;

    private String city;
    private String district;
    private String duration;     // ex: "Day Trip", "3-Day Trip"
    private String style;        // ex: "Nature", "Activity"

    private Boolean isFavorite; // Favorite or not

    private List<String> transport;   // ex: "Public Transport", "Bicycle"

    private List<TravelScheduleDto> scheduleList;    // Schedule list by DAY
}