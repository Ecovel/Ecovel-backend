package com.example.ecovel_server.entity;

// 일자별 비교용 세부 테이블

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonFootprintDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상위 리포트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private TravelReport report;

    // 해당 Day ("Day 1", "Day 2")
    private int day;

    // 이동 수단 (버스, 도보 등) -> 여러 개 선택 가능
    private String transportMode;

    // 차량만 썼을 경우 탄소량 (예측치)
    private Double vehicleCarbon;

    // 실제 탄소량 (선택 교통수단 기준)
    private Double actualCarbon;
}