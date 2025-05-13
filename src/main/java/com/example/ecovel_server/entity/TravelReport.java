package com.example.ecovel_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 하나의 리포트는 하나의 여행 계획(TravelPlan)에 대응
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private TravelPlan travelPlan;

    // 차량만 이용했을 때 예상되는 총 배출량
    private Double expectedCarbon;

    // 실제 사용한 이동 수단 기준 탄소량
    private Double actualCarbon;

    // 절감량 (expected - actual)
    private Double reducedCarbon;

    // 점수화한 친환경 점수
    private Integer ecoScore;

}