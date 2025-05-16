package com.example.ecovel_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TravelAIRequest {
    private String city;        // ex: "Jeju"
    private String district;    // ex: "Jeju-si" or "Random"
    private String duration;    // ex: "3-Day Trip"
    private String style;       // ex: "Nature"

    private List<String> transport; // ex: "Bicycle"
}