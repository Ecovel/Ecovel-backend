package com.example.ecovel_server.dto;

import lombok.*;
import java.util.List;

//클라이언트가 DAY 넘겨주기

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonEstimateRequest {
    private Long planId;  // 분석 대상 TravelPlan ID
    private List<TravelScheduleDto> schedules; //여기서 DAY 정보 추출
}