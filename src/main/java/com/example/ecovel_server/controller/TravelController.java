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

@RestController
@RequestMapping("/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 1. Travel recommendations
    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<TravelRecommendResponse>> recommend(
            @RequestBody TravelRecommendRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("no user"))
                    .getId();

            TravelRecommendResponse response = travelService.recommendTravelPlan(request, userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 2. Provide options
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<TravelOptionResponse>> getOptions() {
        try {
            TravelOptionResponse options = travelService.getTravelOptions(); //dto의 response 호출
            return ResponseEntity.ok(ApiResponse.success(options));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 3. Travel details inquiry
    @GetMapping("/details/{planId}")
    public ResponseEntity<ApiResponse<TravelRecommendResponse>> getTravelDetails(
            @PathVariable Long planId,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("no user"))
                    .getId();

            TravelRecommendResponse response = travelService.getTravelPlanDetails(planId, userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }


    // 4. Check list of city/province districts
    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<DistrictResponse>> getDistricts(@RequestParam String city) {
        try {
            DistrictResponse response = travelService.getDistrictsByCity(city);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // Add Travel Status Change API
    @PostMapping("/status/{planId}")
    public ResponseEntity<ApiResponse<String>> updateStatus(@PathVariable Long planId,
                                                            @RequestParam TravelStatus status) {
        try {
            travelService.updatePlanStatus(planId, status);
            return ResponseEntity.ok(ApiResponse.success("Travel Status Update Completed"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // Add Travel List Inquiry API by Status
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<FavoriteTravelResponse>>> getByStatus(@RequestParam TravelStatus status) {
        try {
            List<FavoriteTravelResponse> list = travelService.getTravelPlansByStatus(status); //즐겨찾기 목록과 연관
            return ResponseEntity.ok(ApiResponse.success(list));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // Add Favorite
    @PostMapping("/favorites")
    public ResponseEntity<ApiResponse<String>> addFavorite(
            @RequestHeader("Authorization") String token,
            @RequestParam Long planId) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("no user"))
                    .getId();

            travelService.addFavorite(planId, userId);
            return ResponseEntity.ok(ApiResponse.success("Finished adding favorites"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // Full Favorite View
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<FavoriteTravelResponse>>> getFavorites(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            Long userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("no user"))
                    .getId();

            return ResponseEntity.ok(ApiResponse.success(travelService.getFavorites(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // Delete Favorite
    @DeleteMapping("/favorites/{favoriteId}")
    public ResponseEntity<ApiResponse<String>> deleteFavorite(@PathVariable Long favoriteId) {
        try {
            travelService.deleteFavorite(favoriteId);
            return ResponseEntity.ok(ApiResponse.success("Finished deleting favorites"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
