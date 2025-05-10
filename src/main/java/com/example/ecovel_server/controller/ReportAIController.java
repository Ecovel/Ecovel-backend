package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.ApiResponse;
import com.example.ecovel_server.dto.CarbonEstimateRequest;
import com.example.ecovel_server.dto.TravelReportResponseDto;
import com.example.ecovel_server.service.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai/carbon")
@RequiredArgsConstructor
public class ReportAIController {

    private final AIClient aiClient;

    // 여행 일정 기반 탄소 배출량 분석 요청 (AI 서버로 중계)
    @PostMapping("/estimate")
    public ApiResponse<TravelReportResponseDto> getCarbonEstimate(@RequestBody CarbonEstimateRequest request) {
        return ApiResponse.success(aiClient.getCarbonEstimate(request));
    }
}
