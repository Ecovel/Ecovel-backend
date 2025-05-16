package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.ApiResponse;
import com.example.ecovel_server.dto.TravelAIRequest;
import com.example.ecovel_server.dto.TravelAIResponse;
import com.example.ecovel_server.service.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//test

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class TravelAIController {

    private final AIClient aiClient;

    @PostMapping("/recommend")
    public ResponseEntity<TravelAIResponse> recommendByAI(@RequestBody TravelAIRequest request) {
        TravelAIResponse response = aiClient.getRecommendation(request);
        return ResponseEntity.ok(response);
    }

}