package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.MissionReport;
import com.example.ecovel_server.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MissionReportRepository extends JpaRepository<MissionReport, Long> {

    List<MissionReport> findByTravelPlan(TravelPlan travelPlan);

    Optional<MissionReport> findByTravelPlanAndDayAndPlaceId(TravelPlan travelPlan, int day, String placeId);

    Optional<MissionReport> findByTravelPlanAndDay(TravelPlan travelPlan, int day);
}