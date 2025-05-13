package com.example.ecovel_server.service;


import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.entity.*;
import com.example.ecovel_server.repository.MissionReportRepository;
import com.example.ecovel_server.repository.TravelPlanRepository;
import com.example.ecovel_server.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final FileStorageUtil fileStorageUtil;
    private final AIClient aiClient;
    private final TravelPlanRepository travelPlanRepository;
    private final MissionReportRepository missionReportRepository;

    public MissionResultResponse verifyAndSaveMission(
            Long userId, // 추가
            Long planId,
            int day,
            String placeId,
            MultipartFile image,
            String userFaceUrl
    ) {

        // 1. 여행 계획 조회
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        // 이미지 저장 후 URL 획득 (미션 업로드 사진)
        String imageUrl = fileStorageUtil.saveImageAndReturnUrl(image, planId, day, placeId);

        // 3. 등록된 얼굴 이미지 다운로드 (마이페이지 사진)
        File downloadedFaceImage = fileStorageUtil.downloadImageFromUrl(userFaceUrl, planId, day, placeId);

        // 4. AI 서버 요청 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("selfieImage", image.getResource()); // 셀카
        body.add("registeredImage", new FileSystemResource(downloadedFaceImage)); // 등록 얼굴

        body.add("placeId", placeId);
        body.add("day", String.valueOf(day));

        // 4. AI 서버에 사진 인증 요청
        MissionImageResponse aiResult = aiClient.verifyMissionImage(body);

        // 5. 결과 저장
        MissionReport report = MissionReport.builder()
                .travelPlan(plan)
                .day(day)
                .placeId(placeId)
                .imageUrl(imageUrl) //사용자가 업로드한 셀카
                .result(aiResult.getResult())
                .verifiedAt(LocalDateTime.now())
                .build();
        missionReportRepository.save(report);

        // 6. 결과 반환
        return MissionResultResponse.builder()
                .result(report.getResult())
                .imageUrl(report.getImageUrl())
                .build();
    }

    // MissionService.java 내부에 추가
    public MissionResultResponse getMissionStatus(Long planId, int day, String placeId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        MissionReport report = missionReportRepository.findByTravelPlanAndDayAndPlaceId(plan, day, placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜와 장소에 대한 인증 결과가 없습니다."));

        return MissionResultResponse.builder()
                .result(report.getResult())
                .imageUrl(report.getImageUrl())
                .build();
    }

    // MissionService.java
    public List<MissionResultResponse> getMissionHistory(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        List<MissionReport> reports = missionReportRepository.findByTravelPlan(plan);

        return reports.stream().map(report -> MissionResultResponse.builder()
                .result(report.getResult())
                .imageUrl(report.getImageUrl())
                .build()).toList();
    }

    // 여행 시작 → 상태: ONGOING
    public void startMission(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        plan.setStatus(TravelStatus.ONGOING); // 상태 변경 추가
        travelPlanRepository.save(plan);
    }

    // 여행 완료 → 상태: COMPLETED
    public void completeMission(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        plan.setStatus(TravelStatus.COMPLETED); // 상태 변경
        travelPlanRepository.save(plan);
    }

    // 상태별 조회 → Controller에서 재사용
    public List<FavoriteTravelResponse> getPlansByStatus(TravelStatus status, Long userId) {
        List<TravelPlan> plans = travelPlanRepository.findByStatusAndUserId(status, userId);
        if (plans == null || plans.isEmpty()) {
            return List.of(); // 빈 리스트로 반환
        }

        return plans.stream()
                .filter(plan -> plan != null) // 혹시 모를 null plan 방지
                .map(plan -> {
                    return FavoriteTravelResponse.builder()
                            .favoriteId(null)
                            .planId(plan.getId())
                            .city(plan.getCity())
                            .district(plan.getDistrict())
                            .duration(plan.getDuration())
                            .style(plan.getStyle())
                            .transport(plan.getTransport())
                            .build();
                }).toList();
    }

    //날짜별 미션 장소 좌표 조회 API 추가
    public List<MissionLocationResponseDto> getMissionLocations(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        return plan.getScheduleList().stream()
                .flatMap(schedule -> schedule.getPlaces().stream()
                        .map(place -> MissionLocationResponseDto.builder()
                                .day(schedule.getDay())
                                .placeName(place.getName())
                                .latitude(place.getLatitude())
                                .longitude(place.getLongitude())
                                .build()))
                .toList();
    }


    // 하루 단위 미션 날짜와 인증 여부 확인
    public TodayMissionStatusResponseDto getTodayMissionStatus(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        // 오늘이 며칠차인지 계산 (예: DAY 1 → 1일차)
        int dayNumber = (int) ChronoUnit.DAYS.between(plan.getStartDate(), LocalDate.now()) + 1;

        // 오늘 인증 여부 확인
        boolean isCompleted = missionReportRepository
                .findByTravelPlanAndDay(plan, dayNumber)
                .isPresent();

        return TodayMissionStatusResponseDto.builder()
                .day(dayNumber)
                .isCompleted(isCompleted)
                .build();
    }

    // 하루 단위 미션 인증 내용
    public TodayMissionContentResponseDto getTodayMissionContent(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("여행 계획을 찾을 수 없습니다."));

        int dayNumber = (int) ChronoUnit.DAYS.between(plan.getStartDate(), LocalDate.now()) + 1;

        TravelSchedule todaySchedule = plan.getScheduleList().stream()
                .filter(s -> s.getDay() == dayNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 일정이 없습니다."));

        // 첫 번째 장소 기준
        TravelPlace mainPlace = todaySchedule.getPlaces().get(0);

        return TodayMissionContentResponseDto.builder()
                .day(dayNumber)
                .placeName(mainPlace.getName())
                .description("오늘의 미션: " + mainPlace.getName() + "에서 인증샷을 찍어주세요!")
                .build();
    }
}