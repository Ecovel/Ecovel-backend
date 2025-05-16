package com.example.ecovel_server.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonFootprintDetailDto {
    private String day;
    private String transportMode;
    private Double vehicleCarbon;
    private Double actualCarbon;
}
