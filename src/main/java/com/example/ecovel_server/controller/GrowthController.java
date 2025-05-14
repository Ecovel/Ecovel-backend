package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.ApiResponse;
import com.example.ecovel_server.dto.GrowthLogResponseDto;
import com.example.ecovel_server.service.GrowthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/growth")
@RequiredArgsConstructor
public class GrowthController {

    private final GrowthService growthService;

    @GetMapping("/log")
    public ResponseEntity<ApiResponse<GrowthLogResponseDto>> getGrowthLog(
            @RequestParam("userId") Long userId
    ) {
        GrowthLogResponseDto response = growthService.getGrowthLog(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}