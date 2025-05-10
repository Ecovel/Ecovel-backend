package com.example.ecovel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TravelOptionResponse {
    private List<String> durations;
    private List<String> styles;
    private List<String> transports;
}
