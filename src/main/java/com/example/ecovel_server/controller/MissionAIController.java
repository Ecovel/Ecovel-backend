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
            @RequestParam("image") MultipartFile image, //upload image
            @RequestParam("placeId") String placeId,
            @RequestParam("day") int day,
            @RequestParam("userFaceUrl") String userFaceUrl // user face image
    ) {
        // 1. MultipartBody 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image.getResource());
        body.add("placeId", placeId);
        body.add("day", String.valueOf(day));
        body.add("userFaceUrl", userFaceUrl);

        // 2. AI server
        MissionImageResponse result = aiClient.verifyMissionImage(body);

        // 3. return result
        return ResponseEntity.ok(result);
    }
}

