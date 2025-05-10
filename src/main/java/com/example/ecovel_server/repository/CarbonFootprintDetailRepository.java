package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.CarbonFootprintDetail;
import com.example.ecovel_server.entity.TravelReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarbonFootprintDetailRepository extends JpaRepository<CarbonFootprintDetail, Long> {

    // 특정 리포트에 연결된 모든 detail 조회
    List<CarbonFootprintDetail> findByReport(TravelReport report);
}
