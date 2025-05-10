package com.example.ecovel_server.service;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.entity.*;
import com.example.ecovel_server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AIClient aiClient;
    private final TravelReportRepository travelReportRepository;
    private final CarbonFootprintDetailRepository carbonDetailRepository;
    private final TravelPlanRepository travelPlanRepository;

    /** 1. 탄소 분석 요청 (AI 호출 전 여행 상태 검증) */
    public TravelReportResponseDto analyzeReport(CarbonEstimateRequest request) {
        TravelPlan plan = travelPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        if (plan.getStatus() != TravelStatus.COMPLETED) {
            throw new IllegalStateException("완료된 여행에 대해서만 탄소 분석이 가능합니다.");
        }

        return aiClient.getCarbonEstimate(request); //어쨋든 AI 서버에서 받아온 결과를 응답한다.
    }

    /** 2. 분석 결과 저장 (여행 상태 검증 포함) */
    public void saveReport(TravelReportResponseDto dto) {
        TravelPlan plan = travelPlanRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid planId"));

        if (plan.getStatus() != TravelStatus.COMPLETED) {
            throw new IllegalStateException("여행이 완료된 상태에서만 리포트를 저장할 수 있습니다.");
        }

        // 1. 리포트 저장
        TravelReport report = TravelReport.builder()
                .travelPlan(plan)
                .expectedCarbon(dto.getExpectedCarbon())
                .actualCarbon(dto.getActualCarbon())
                .reducedCarbon(dto.getReducedCarbon())
                .ecoScore(dto.getEcoScore())
                .summary(dto.getSummary())
                .build();

        travelReportRepository.save(report);

        // 2. 세부 내용 저장
        List<CarbonFootprintDetail> details = dto.getDetails().stream().map(d ->
                CarbonFootprintDetail.builder()
                        .report(report)
                        .day(Integer.parseInt(d.getDay().replaceAll("[^0-9]", "")))
                        .transportMode(d.getTransportMode())
                        .vehicleCarbon(d.getVehicleCarbon())
                        .actualCarbon(d.getActualCarbon())
                        .build()
        ).collect(Collectors.toList());

        carbonDetailRepository.saveAll(details);
    }

    /** 3. 리포트 단건 조회 */
    public TravelReportResponseDto getReportByPlan(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid planId"));

        TravelReport report = travelReportRepository.findByTravelPlan(plan)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        List<CarbonFootprintDetail> details = carbonDetailRepository.findByReport(report);

        return TravelReportResponseDto.builder()
                .reportId(report.getId())
                .planId(planId)
                .expectedCarbon(report.getExpectedCarbon())
                .actualCarbon(report.getActualCarbon())
                .reducedCarbon(report.getReducedCarbon())
                .ecoScore(report.getEcoScore())
                .summary(report.getSummary())
                .details(details.stream().map(d ->
                        CarbonFootprintDetailDto.builder()
                                .day(d.getDay())
                                .transportMode(d.getTransportMode())
                                .vehicleCarbon(d.getVehicleCarbon())
                                .actualCarbon(d.getActualCarbon())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

    /** 4. 전체 리포트 리스트 조회 (COMPLETED 여행만) */
    public List<TravelReportResponseDto> getAllReports() {
        List<TravelReport> reports = travelReportRepository.findAll();

        // 필터링: COMPLETED 상태의 여행만
        return reports.stream()
                .filter(r -> r.getTravelPlan().getStatus() == TravelStatus.COMPLETED)
                .map(r -> TravelReportResponseDto.builder()
                        .reportId(r.getId())
                        .planId(r.getTravelPlan().getId())
                        .expectedCarbon(r.getExpectedCarbon())
                        .actualCarbon(r.getActualCarbon())
                        .reducedCarbon(r.getReducedCarbon())
                        .ecoScore(r.getEcoScore())
                        .build()
                ).collect(Collectors.toList());
    }
}
