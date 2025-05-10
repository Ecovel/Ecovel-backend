package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.TravelPlan;
import com.example.ecovel_server.entity.TravelStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    List<TravelPlan> findByStatus(TravelStatus status);
}
