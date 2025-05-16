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

    @PostMapping("/analyze")
    public ApiResponse<TravelReportResponseDto> analyzeReport(@RequestBody CarbonEstimateRequest request) {
        TravelReportResponseDto response = reportService.analyzeReport(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/save")
    public ApiResponse<Void> saveReport(@RequestBody TravelReportResponseDto dto) {
        reportService.saveReport(dto);
        return ApiResponse.success(null);
    }

    @GetMapping("/{planId}")
    public ApiResponse<TravelReportResponseDto> getReport(@PathVariable Long planId) {
        TravelReportResponseDto response = reportService.getReportByPlan(planId);
        return ApiResponse.success(response);
    }

    @GetMapping("/list")
    public ApiResponse<List<TravelReportResponseDto>> getAllReports() {
        List<TravelReportResponseDto> response = reportService.getAllReports();
        return ApiResponse.success(response);
    }
}