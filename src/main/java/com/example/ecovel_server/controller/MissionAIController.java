package com.example.ecovel_server.controller;

//테스트용

import com.example.ecovel_server.dto.MissionImageResponse;
import com.example.ecovel_server.service.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class MissionAIController {

    private final AIClient aiClient;

    /**
     * test API
     */
    @PostMapping("/verify-image")
    public ResponseEntity<MissionImageResponse> verifyImageDirect(
            @RequestParam("image") MultipartFile image, //업로드한 미션 사진
            @RequestParam("placeId") String placeId,
            @RequestParam("day") int day,
            @RequestParam("userFaceUrl") String userFaceUrl //사용자 기존 얼굴 이미지
    ) {
        // 1. MultipartBody 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image.getResource()); // RestTemplate는 File 또는 Resource 사용 가능
        body.add("placeId", placeId);
        body.add("day", String.valueOf(day));
        body.add("userFaceUrl", userFaceUrl);

        // 2. AI 서버 호출
        MissionImageResponse result = aiClient.verifyMissionImage(body);

        // 3. 결과 반환
        return ResponseEntity.ok(result);
    }
}

