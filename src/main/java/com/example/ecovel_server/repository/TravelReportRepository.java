package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.TravelReport;
import com.example.ecovel_server.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TravelReportRepository extends JpaRepository<TravelReport, Long> {

    // 여행 계획(TravelPlan)으로 리포트 조회
    Optional<TravelReport> findByTravelPlan(TravelPlan travelPlan);
}
