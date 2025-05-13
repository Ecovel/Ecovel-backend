package com.example.ecovel_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteTravel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 즐겨찾기된 여행 계획
    @OneToOne
    @JoinColumn(name = "plan_id")
    private TravelPlan travelPlan;

    //사용자 정보 추가
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}