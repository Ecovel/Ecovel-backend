package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.CarbonFootprintDetail;
import com.example.ecovel_server.entity.TravelReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarbonFootprintDetailRepository extends JpaRepository<CarbonFootprintDetail, Long> {

    // Look up all the details associated with a specific report
    List<CarbonFootprintDetail> findByReport(TravelReport report);
}
