package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.ApiResponse;
import com.example.ecovel_server.dto.TravelAIRequest;
import com.example.ecovel_server.dto.TravelAIResponse;
import com.example.ecovel_server.service.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//테스트용

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class TravelAIController {

    private final AIClient aiClient;

    // AI 추천 결과만 반환 (DB 저장 없음)
    @PostMapping("/recommend")
    public ResponseEntity<TravelAIResponse> recommendByAI(@RequestBody TravelAIRequest request) {
        TravelAIResponse response = aiClient.getRecommendation(request);
        return ResponseEntity.ok(response);
    }

    // 5. 장소명 기반 이미지 검색
//    @GetMapping("/image")
//    public ResponseEntity<String> getPlaceImage(@RequestParam String place) {
//        String imageUrl = aiClient.getImageByPlace(place);
//        return ResponseEntity.ok(imageUrl);
//    }
}