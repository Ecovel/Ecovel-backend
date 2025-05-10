package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.TravelPlan;
import com.example.ecovel_server.entity.TravelSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TravelScheduleRepository extends JpaRepository<TravelSchedule, Long> {
    List<TravelSchedule> findByTravelPlan(TravelPlan plan);
}