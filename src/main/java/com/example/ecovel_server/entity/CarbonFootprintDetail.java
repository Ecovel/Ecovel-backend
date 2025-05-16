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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private TravelReport report;

    // ex ("Day 1", "Day 2")
    private String day;

    // Transportation (bus, foot, etc.) -> Multiple choices available
    private String transportMode;

    // Carbon content (estimated) if only the vehicle is used
    private Double vehicleCarbon;

    // Actual Carbon Amount (Based on Selective Transportation)
    private Double actualCarbon;
}