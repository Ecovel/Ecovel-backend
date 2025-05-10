package com.example.ecovel_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TravelAIRequest {
    private String city;        // 예: "Jeju"
    private String district;    // 예: "Jeju-si" or "Random"
    private String duration;    // 예: "3-Day Trip"
    private String style;       // 예: "Nature"

    //중복 선택 가능
    private List<String> transport; // 예: "Bicycle"
}