package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** AI 서버에 탄소 분석 요청 */
    @PostMapping("/analyze")
    public ApiResponse<TravelReportResponseDto> analyzeReport(@RequestBody CarbonEstimateRequest request) {
        TravelReportResponseDto response = reportService.analyzeReport(request);
        return ApiResponse.success(response);
    }

    /** 분석 결과 DB에 저장 */
    @PostMapping("/save")
    public ApiResponse<Void> saveReport(@RequestBody TravelReportResponseDto dto) {
        reportService.saveReport(dto);
        return ApiResponse.success(null);  // 성공만 알리면 됨
    }

    /** 특정 여행(planId)의 리포트 조회 */
    @GetMapping("/{planId}")
    public ApiResponse<TravelReportResponseDto> getReport(@PathVariable Long planId) {
        TravelReportResponseDto response = reportService.getReportByPlan(planId);
        return ApiResponse.success(response);
    }

    /** 전체 리포트 리스트 조회 */
    @GetMapping("/list")
    public ApiResponse<List<TravelReportResponseDto>> getAllReports() {
        List<TravelReportResponseDto> response = reportService.getAllReports();
        return ApiResponse.success(response);
    }
}