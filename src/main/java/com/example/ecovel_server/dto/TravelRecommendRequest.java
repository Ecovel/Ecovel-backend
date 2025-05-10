package com.example.ecovel_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TravelRecommendRequest {
    private String city;         // 예: "Jeju"
    private String district;     // 예: "Jeju" 또는 "Random"
    private String duration;     // 예: "Day Trip", "3-Day Trip"
    private String style;        // 예: "Nature", "Activity"

    //중복 선택 가능
    private List<String> transport;   // 예: "Public Transport", "Bicycle"
}