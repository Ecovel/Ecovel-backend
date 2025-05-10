package com.example.ecovel_server.dto;

import lombok.*;

//TravelPlanDto와 똑같은 구성을 가지고 있기 때문에 저 파일은 없어도 된다.

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelRecommendResponse {
    private String city;
    private String district;
    private String duration;     // 예: "Day Trip", "3-Day Trip"
    private String style;        // 예: "Nature", "Activity"

    //중복 선택 가능
    private List<String> transport;   // 예: "Public Transport", "Bicycle"

    private List<TravelScheduleDto> scheduleList;    // DAY별 일정 목록
}