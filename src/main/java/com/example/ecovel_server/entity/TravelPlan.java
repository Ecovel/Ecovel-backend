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
    private Long id; //각 여행 계획의 고유 식별자

    private LocalDate startDate;

    private String city;         // 선택된 시도
    private String district;     // 선택된 구/군
    private String duration;
    private String style;

    @ElementCollection
    private List<String> transport;

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelSchedule> scheduleList; //이 여행 계획에 연결된 여러 개의 일정 정보 리스트

    // 여행 상태 필드 추가
    @Enumerated(EnumType.STRING)
    private TravelStatus status;

    // JPA 저장 시 기본값 설정
    @PrePersist
    public void setDefaultStatus() {
        if (status == null) this.status = TravelStatus.PLANNED;
    }
}