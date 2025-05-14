package com.example.ecovel_server.dto;

//여행지 하나의 정보

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelPlaceDto {
    private String name; //이름

    private String imageUrl;

    //각각 걸리는 시간
    private Integer walkTime;
    private Integer bicycleTime;
    private Integer publicTime;
    private Integer carTime;
}
