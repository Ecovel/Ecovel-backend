package com.example.ecovel_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TravelRecommendRequest {
    private String city;         // ex: "Jeju"
    private String district;     // ex: "Jeju" 또는 "Random"
    private String duration;     // ex: "Day Trip", "3-Day Trip"
    private String style;        // ex: "Nature", "Activity"

    private List<String> transport;   // ex: "Public Transport", "Bicycle"
}