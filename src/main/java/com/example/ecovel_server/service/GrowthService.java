package com.example.ecovel_server.service;

import com.example.ecovel_server.dto.GrowthLogResponseDto;
import com.example.ecovel_server.entity.GrowthLog;
import com.example.ecovel_server.entity.User;
import com.example.ecovel_server.repository.GrowthLogRepository;
import com.example.ecovel_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GrowthService {

    private final GrowthLogRepository growthLogRepository;
    private final UserRepository userRepository;

    // 1. 성장 상태 조회
    public GrowthLogResponseDto getGrowthLog(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return growthLogRepository.findByUser(user)
                .map(growthLog -> GrowthLogResponseDto.builder()
                        .growthStage(growthLog.getGrowthStage())
                        .totalQuizSuccessCount(growthLog.getTotalQuizSuccessCount())
                        .totalMissionSuccessCount(growthLog.getTotalMissionSuccessCount())
                        .totalCarbonSaved(growthLog.getTotalCarbonSaved())
                        .build())
                .orElse(GrowthLogResponseDto.builder()
                        .growthStage("씨앗")            // 기본 성장 단계
                        .totalQuizSuccessCount(0)
                        .totalMissionSuccessCount(0)
                        .totalCarbonSaved(0.0)
                        .build());
    }

    // 2. 미션 성공 시 성장 로그 업데이트
    public void updateGrowthLogAfterMissionSuccess(Long userId, Double carbonSaved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        GrowthLog growthLog = growthLogRepository.findByUser(user)
                .orElseGet(() -> growthLogRepository.save(
                        GrowthLog.builder()
                                .user(user)
                                .totalMissionSuccessCount(0)
                                .totalQuizSuccessCount(0)
                                .totalCarbonSaved(0.0)
                                .growthStage("씨앗")
                                .build()
                ));

        growthLog.setTotalMissionSuccessCount(growthLog.getTotalMissionSuccessCount() + 1);
        growthLog.setTotalCarbonSaved(growthLog.getTotalCarbonSaved() + carbonSaved);

        // 간단한 성장 단계 갱신 로직
        int totalSuccess = growthLog.getTotalMissionSuccessCount() + growthLog.getTotalQuizSuccessCount();
        if (totalSuccess >= 10) {
            growthLog.setGrowthStage("성장기");
        } else if (totalSuccess >= 5) {
            growthLog.setGrowthStage("새싹");
        }

        growthLogRepository.save(growthLog);
    }

    public void updateGrowthLogAfterQuizSuccess(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        GrowthLog growthLog = growthLogRepository.findByUser(user)
                .orElseGet(() -> growthLogRepository.save(
                        GrowthLog.builder()
                                .user(user)
                                .totalMissionSuccessCount(0)
                                .totalQuizSuccessCount(0)
                                .totalCarbonSaved(0.0)
                                .growthStage("씨앗")
                                .build()
                ));

        growthLog.setTotalQuizSuccessCount(growthLog.getTotalQuizSuccessCount() + 1);

        int totalSuccess = growthLog.getTotalMissionSuccessCount() + growthLog.getTotalQuizSuccessCount();
        if (totalSuccess >= 10) {
            growthLog.setGrowthStage("성장기");
        } else if (totalSuccess >= 5) {
            growthLog.setGrowthStage("새싹");
        }

        growthLogRepository.save(growthLog);
    }
}
