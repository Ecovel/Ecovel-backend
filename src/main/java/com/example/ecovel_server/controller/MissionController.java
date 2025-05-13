package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.entity.TravelStatus;
import com.example.ecovel_server.repository.UserRepository;
import com.example.ecovel_server.service.MissionService;
import com.example.ecovel_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/mission")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 1. 사진 인증 업로드
    @PostMapping("/{planId}/verify")
    public ResponseEntity<ApiResponse<MissionResultResponse>> uploadMissionImage(
            @PathVariable Long planId,
            @RequestParam("userId") Long userId, // 추가
            @RequestParam("day") int day,
            @RequestParam("placeId") String placeId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("userFaceUrl") String userFaceUrl
    ) {
        MissionResultResponse response = missionService.verifyAndSaveMission(
                userId, planId, day, placeId, image, userFaceUrl
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 2. 인증 결과 단건 조회
    @GetMapping("/{planId}/status")
    public ResponseEntity<ApiResponse<MissionResultResponse>> getMissionStatus(
            @PathVariable Long planId,
            @RequestParam("day") int day,
            @RequestParam("placeId") String placeId
    ) {
        MissionResultResponse response = missionService.getMissionStatus(planId, day, placeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 3. 인증 히스토리 전체 조회
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<MissionResultResponse>>> getMissionHistory(
            @RequestParam("planId") Long planId
    ) {
        List<MissionResultResponse> history = missionService.getMissionHistory(planId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    // 4. 여행 시작 → 상태: PLANNED → ONGOING
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<String>> startMission(@RequestParam("planId") Long planId) {
        missionService.startMission(planId);
        return ResponseEntity.ok(ApiResponse.success("여행이 시작되었습니다."));
    }

    // 5. 여행 완료 처리 → 상태: ONGOING → COMPLETED
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<String>> completeMission(@RequestParam("planId") Long planId) {
        missionService.completeMission(planId);
        return ResponseEntity.ok(ApiResponse.success("여행이 완료되었습니다."));
    }

    // 6. 예정된 여행 조회 → PLANNED
    @GetMapping("/scheduled")
    public ResponseEntity<ApiResponse<List<FavoriteTravelResponse>>> getScheduled(
            @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"))
                .getId();

        return ResponseEntity.ok(ApiResponse.success(
                missionService.getPlansByStatus(TravelStatus.PLANNED, userId)
        ));
    }

    // 7. 진행 중인 여행 조회 → ONGOING
    @GetMapping("/ongoing")
    public ResponseEntity<ApiResponse<List<FavoriteTravelResponse>>> getOngoing(
            @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"))
                .getId();

        return ResponseEntity.ok(ApiResponse.success(
                missionService.getPlansByStatus(TravelStatus.ONGOING, userId)
        ));
    }

    // 8. 완료된 여행 조회 → COMPLETED
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<List<FavoriteTravelResponse>>> getCompleted(
            @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"))
                .getId();

        return ResponseEntity.ok(ApiResponse.success(
                missionService.getPlansByStatus(TravelStatus.COMPLETED, userId)
        ));
    }

    // 위치 정보
    @GetMapping("/{planId}/locations")
    public ResponseEntity<ApiResponse<List<MissionLocationResponseDto>>> getMissionLocations(
            @PathVariable Long planId
    ) {
        List<MissionLocationResponseDto> locations = missionService.getMissionLocations(planId);
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    // 하루 단위의 미션 날짜와 인증 여부 확인
    @GetMapping("/{planId}/today-status")
    public ResponseEntity<ApiResponse<TodayMissionStatusResponseDto>> getTodayStatus(
            @PathVariable Long planId
    ) {
        TodayMissionStatusResponseDto status = missionService.getTodayMissionStatus(planId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    //하루 단위 미션 내용
    @GetMapping("/{planId}/today")
    public ResponseEntity<ApiResponse<TodayMissionContentResponseDto>> getTodayMissionContent(
            @PathVariable Long planId
    ) {
        TodayMissionContentResponseDto response = missionService.getTodayMissionContent(planId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
