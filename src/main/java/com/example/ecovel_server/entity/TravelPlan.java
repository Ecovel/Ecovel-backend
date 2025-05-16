package com.example.ecovel_server.entity;

//여행 전체 계획

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity //이 클래스가 JPA 엔티티임을 의미 -> DB 데이블로 매핑됨

// Lombok 어노테이션
@Getter
@Setter

@NoArgsConstructor //기본 생성자 생성
@AllArgsConstructor //모든 필드를 매개변수로 갖는 생성자 자동 생성
@Builder //객체 생성 가능하게 함
public class TravelPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;

    private String city;
    private String district;
    private String duration;
    private String style;

    @ElementCollection
    private List<String> transport;

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelSchedule> scheduleList; //Multiple calendar information lists associated with this travel plan

    // Add Travel Status Field
    @Enumerated(EnumType.STRING)
    private TravelStatus status;

    // JPA Save Defaults
    @PrePersist
    public void setDefaultStatus() {
        if (status == null) this.status = TravelStatus.PLANNED;
    }

    //Adding User Information
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}