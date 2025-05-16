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

    // Favorite Travel Plan
    @OneToOne
    @JoinColumn(name = "plan_id")
    private TravelPlan travelPlan;

    //Adding User Information
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}