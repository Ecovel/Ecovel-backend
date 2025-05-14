package com.example.ecovel_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrowthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저와 1:1 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // 총 퀴즈 성공 횟수
    private int totalQuizSuccessCount;

    // 총 미션 성공 횟수
    private int totalMissionSuccessCount;

    // 누적 탄소 절감량 (g 단위로 저장)
    private double totalCarbonSaved;

    // 성장 단계 (예: 씨앗, 새싹 등 텍스트로 저장)
    private String growthStage;

    // 생성일, 수정일 자동 처리
    @Column(updatable = false)
    private java.time.LocalDate createdAt;
    private java.time.LocalDate updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = java.time.LocalDate.now();
        this.updatedAt = java.time.LocalDate.now();
        if (this.growthStage == null) {
            this.growthStage = "씨앗"; // 초기값
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = java.time.LocalDate.now();
    }
}