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

    private String name;
    private String district;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // Travel time (minutes) by means of transportation
    private Integer walkTime;
    private Integer bicycleTime;
    private Integer publicTime;
    private Integer carTime;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private TravelSchedule schedule;
}