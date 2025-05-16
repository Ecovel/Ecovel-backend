package com.example.ecovel_server.dto;

import lombok.*;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonEstimateRequest {
    private Long planId;
    private List<TravelScheduleDto> schedules;
}