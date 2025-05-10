package com.example.ecovel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DistrictResponse {
    private List<String> districts;
}