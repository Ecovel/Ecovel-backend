package com.example.ecovel_server.entity;

//여행 장소(TravelSchedule에 여러 장소가 포함된다.)

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;         // 장소명
    private String district;     // 구/군 -> plan에 있는데 여기에도 꼭 필요할까? TravelPlace 객체만으로도 어느 지역인지 파악할 수 있음

    private String imageUrl;

    // 교통 수단별 이동 시간 (분 단위)
    private Integer walkTime;        // 도보 이동 시간
    private Integer bicycleTime;     // 자전거 이동 시간
    private Integer publicTime;      // 대중교통 이동 시간
    private Integer carTime;         // 자동차 이동 시간 (선택적)

    @Column(name = "latitude")
    private Double latitude; //위도

    @Column(name = "longitude")
    private Double longitude; //경도

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private TravelSchedule schedule;
}