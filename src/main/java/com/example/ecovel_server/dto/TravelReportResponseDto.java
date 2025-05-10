package com.example.ecovel_server.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelReportResponseDto {
    private Long reportId;
    private Long planId;
    private Double expectedCarbon; //차로만 이동했을 때의 탄소 배출량
    private Double actualCarbon; //사용자의 실제 탄소 배출량
    private Double reducedCarbon; //감소한 탄소 배출량
    private Integer ecoScore; //친환경 점수
    private String summary;

    private List<CarbonFootprintDetailDto> details;
}
