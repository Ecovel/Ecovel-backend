package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.TravelPlace;
import com.example.ecovel_server.entity.TravelSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long> {
    List<TravelPlace> findBySchedule(TravelSchedule schedule);
}