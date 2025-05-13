package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.repository.UserRepository;
import com.example.ecovel_server.service.TravelService;
import com.example.ecovel_server.entity.TravelStatus;
import com.example.ecovel_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //JSON 기반 REST API 컨트롤러임을 명시
@RequestMapping("/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 1. 여행 추천
    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<TravelRecommendResponse>> recommend(
            @RequestBody TravelRecommendRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자 없음"))
                    .getId();

            TravelRecommendResponse response = travelService.recommendTravelPlan(request, userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 2. 옵션 제공
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<TravelOptionResponse>> getOptions() {
        try {
            TravelOptionResponse options = travelService.getTravelOptions(); //dto의 response 호출
            return ResponseEntity.ok(ApiResponse.success(options));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 3. 여행 상세 조회
    @GetMapping("/details/{planId}")
    public ResponseEntity<ApiResponse<TravelRecommendResponse>> getTravelDetails(@PathVariable Long planId) {
        try {
            TravelRecommendResponse response = travelService.getTravelPlanDetails(planId); //tripId가 planId랑 같은거 아닌가? 굳이 다른게 쓸 이유가?
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }


    // 4. 시/도별 구 목록 조회
    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<DistrictResponse>> getDistricts(@RequestParam String city) {
        try {
            DistrictResponse response = travelService.getDistrictsByCity(city);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 여행 상태 변경 API 추가
    @PostMapping("/status/{planId}")
    public ResponseEntity<ApiResponse<String>> updateStatus(@PathVariable Long planId,
                                                            @RequestParam TravelStatus status) {
        try {
            travelService.updatePlanStatus(planId, status);
            return ResponseEntity.ok(ApiResponse.success("여행 상태 업데이트 완료"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    //상태별 여행 목록 조회 API 추가
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<FavoriteTravelResponse>>> getByStatus(@RequestParam TravelStatus status) {
        try {
            List<FavoriteTravelResponse> list = travelService.getTravelPlansByStatus(status); //즐겨찾기 목록과 연관
            return ResponseEntity.ok(ApiResponse.success(list));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 5. 즐겨찾기 추가
    @PostMapping("/favorites")
    public ResponseEntity<ApiResponse<String>> addFavorite(
            @RequestHeader("Authorization") String token,
            @RequestParam Long planId) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자 없음"))
                    .getId();

            travelService.addFavorite(planId, userId);
            return ResponseEntity.ok(ApiResponse.success("즐겨찾기 추가 완료"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 6. 즐겨찾기 전체 조회
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<FavoriteTravelResponse>>> getFavorites(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자 정보 없음"))
                    .getId();

            return ResponseEntity.ok(ApiResponse.success(travelService.getFavorites(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 7. 즐겨찾기 삭제
    @DeleteMapping("/favorites/{favoriteId}")
    public ResponseEntity<ApiResponse<String>> deleteFavorite(@PathVariable Long favoriteId) {
        try {
            travelService.deleteFavorite(favoriteId);
            return ResponseEntity.ok(ApiResponse.success("즐겨찾기 삭제 완료"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
