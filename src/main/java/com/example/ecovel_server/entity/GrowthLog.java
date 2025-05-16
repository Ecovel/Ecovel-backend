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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Total number of successful quizzes
    private int totalQuizSuccessCount;

    // Total number of successful missions
    private int totalMissionSuccessCount;

    // Accumulated carbon savings (stored in g)
    private double totalCarbonSaved;

    // Growth stage (e.g., save as text such as seeds, sprouts, etc.)
    private String growthStage;

    // Automatic processing of creation and modification dates
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