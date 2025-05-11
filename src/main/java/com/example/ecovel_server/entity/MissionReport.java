package com.example.ecovel_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 어떤 여행 계획에 속한 인증인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private TravelPlan travelPlan;

    private int day;                // DAY1, DAY2 등
    private String placeId;         // 장소 식별자 (혹은 TravelPlace의 ID)

    private String imageUrl;        // 업로드한 이미지의 URL

    private String result; // 성공 여부

    private LocalDateTime verifiedAt; // 인증 완료 시각
}