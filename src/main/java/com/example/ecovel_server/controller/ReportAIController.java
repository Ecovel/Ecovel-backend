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

    // Request for schedule-based carbon emissions analysis (translated to AI server)
    @PostMapping("/estimate")
    public ApiResponse<TravelReportResponseDto> getCarbonEstimate(@RequestBody CarbonEstimateRequest request) {
        return ApiResponse.success(aiClient.getCarbonEstimate(request));
    }
}
