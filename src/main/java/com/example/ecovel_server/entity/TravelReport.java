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

    // One report corresponds to one TravelPlan
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private TravelPlan travelPlan;

    // Total emissions expected when using vehicles only
    private Double expectedCarbon;

    // Actual amount of carbon used by means of transportation
    private Double actualCarbon;

    // the amount of savings (expected - actual)
    private Double reducedCarbon;

    // Scored Green Scores
    private Integer ecoScore;

}