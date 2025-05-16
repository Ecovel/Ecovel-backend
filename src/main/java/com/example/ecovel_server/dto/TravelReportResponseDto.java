package com.example.ecovel_server.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelReportResponseDto {
    private Long reportId;
    private Long planId;

    private String city;
    private String startDate;

    private Double expectedCarbon; //Carbon emissions when traveling only by car
    private Double actualCarbon; //Actual carbon emissions from users
    private Double reducedCarbon; //reduced carbon emissions
    private Integer ecoScore; //eco-friendly score

    private List<CarbonFootprintDetailDto> details;
}
