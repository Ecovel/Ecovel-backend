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
    private List<TravelPlaceDto> places;             // List of places


    public String getDay() {
        return "DAY " + day;
    }
}