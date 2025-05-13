package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.MissionReport;
import com.example.ecovel_server.entity.TravelPlan;
import com.example.ecovel_server.entity.TravelStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    List<TravelPlan> findByStatus(TravelStatus status);

    List<TravelPlan> findByStatusAndUserId(TravelStatus status, Long userId);
}
