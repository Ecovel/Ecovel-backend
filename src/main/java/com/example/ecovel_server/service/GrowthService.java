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

    // 1. Look up your growth status
    public GrowthLogResponseDto getGrowthLog(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return growthLogRepository.findByUser(user)
                .map(growthLog -> GrowthLogResponseDto.builder()
                        .growthStage(growthLog.getGrowthStage())
                        .totalQuizSuccessCount(growthLog.getTotalQuizSuccessCount())
                        .totalMissionSuccessCount(growthLog.getTotalMissionSuccessCount())
                        .totalCarbonSaved(growthLog.getTotalCarbonSaved())
                        .build())
                .orElse(GrowthLogResponseDto.builder()
                        .growthStage("Seed")
                        .totalQuizSuccessCount(0)
                        .totalMissionSuccessCount(0)
                        .totalCarbonSaved(0.0)
                        .build());
    }

    // 2. Update Growth Logs on Mission Success
    public void updateGrowthLogAfterMissionSuccess(Long userId, Double carbonSaved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found.."));

        GrowthLog growthLog = growthLogRepository.findByUser(user)
                .orElseGet(() -> growthLogRepository.save(
                        GrowthLog.builder()
                                .user(user)
                                .totalMissionSuccessCount(0)
                                .totalQuizSuccessCount(0)
                                .totalCarbonSaved(0.0)
                                .growthStage("Seed")
                                .build()
                ));

        growthLog.setTotalMissionSuccessCount(growthLog.getTotalMissionSuccessCount() + 1);
        growthLog.setTotalCarbonSaved(growthLog.getTotalCarbonSaved() + carbonSaved);

        // Simple Growth Phase Update Logic
        int totalSuccess = growthLog.getTotalMissionSuccessCount() + growthLog.getTotalQuizSuccessCount();
        if (totalSuccess >= 10) {
            growthLog.setGrowthStage("Tree");
        } else if (totalSuccess >= 5) {
            growthLog.setGrowthStage("Seed");
        }

        growthLogRepository.save(growthLog);
    }

    public void updateGrowthLogAfterQuizSuccess(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found.."));

        GrowthLog growthLog = growthLogRepository.findByUser(user)
                .orElseGet(() -> growthLogRepository.save(
                        GrowthLog.builder()
                                .user(user)
                                .totalMissionSuccessCount(0)
                                .totalQuizSuccessCount(0)
                                .totalCarbonSaved(0.0)
                                .growthStage("Seed")
                                .build()
                ));

        growthLog.setTotalQuizSuccessCount(growthLog.getTotalQuizSuccessCount() + 1);

        int totalSuccess = growthLog.getTotalMissionSuccessCount() + growthLog.getTotalQuizSuccessCount();
        if (totalSuccess >= 10) {
            growthLog.setGrowthStage("Tree");
        } else if (totalSuccess >= 5) {
            growthLog.setGrowthStage("Seed");
        }

        growthLogRepository.save(growthLog);
    }
}
