package com.example.ecovel_server.service;


import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.entity.*;
import com.example.ecovel_server.repository.MissionReportRepository;
import com.example.ecovel_server.repository.TravelPlanRepository;
import com.example.ecovel_server.repository.TravelReportRepository;
import com.example.ecovel_server.repository.UserRepository;
import com.example.ecovel_server.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final FileStorageUtil fileStorageUtil;
    private final AIClient aiClient;
    private final TravelPlanRepository travelPlanRepository;
    private final MissionReportRepository missionReportRepository;
    private final TravelReportRepository travelReportRepository;
    private final GrowthService growthService;
    private final UserRepository userRepository;
    private final ReportService reportService;

    public MissionResultResponse verifyAndSaveMission(
            Long userId,
            Long planId,
            int day,
            String placeId,
            MultipartFile image,
            String userFaceUrl
    ) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("No travel plans found."));

        // 1. Save MultipartFile as a physical File
        File selfieFile = fileStorageUtil.saveImageToFile(image, planId, day, placeId);

        // 2. Face image URL → File
        File downloadedFaceImage = fileStorageUtil.downloadImageFromUrl(userFaceUrl, planId, day, placeId);

        // 3. Configure requests to send to the AI server
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("selfieImage", new FileSystemResource(selfieFile));
        body.add("registeredImage", new FileSystemResource(downloadedFaceImage));
        body.add("placeId", placeId);
        body.add("day", String.valueOf(day));

        // 4. Invoke AI Server
        MissionImageResponse aiResult = aiClient.verifyMissionImage(body);

        // 5. Save User and Authentication Results
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        MissionReport report = MissionReport.builder()
                .user(user)
                .travelPlan(plan)
                .day(day)
                .placeId(placeId)
                .imageUrl(selfieFile.getAbsolutePath())
                .result(aiResult.getResult())
                .verifiedAt(LocalDateTime.now())
                .build();

        missionReportRepository.save(report);

        if ("success".equals(aiResult.getResult())) {
            TravelReport travelReport = travelReportRepository.findByTravelPlan(plan)
                    .orElseThrow(() -> new IllegalArgumentException("Travel report not found."));
            Double reducedCarbon = travelReport.getReducedCarbon() == null ? 0.0 : travelReport.getReducedCarbon();
            growthService.updateGrowthLogAfterMissionSuccess(userId, reducedCarbon);
        }

        return MissionResultResponse.builder()
                .result(report.getResult())
                .imageUrl(report.getImageUrl())
                .build();
    }

    public MissionResultResponse getMissionStatus(Long planId, int day, String placeId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("No travel plans found."));

        MissionReport report = missionReportRepository.findByTravelPlanAndDayAndPlaceId(plan, day, placeId)
                .orElseThrow(() -> new IllegalArgumentException("No authentication results for that date and place."));

        return MissionResultResponse.builder()
                .result(report.getResult())
                .imageUrl(report.getImageUrl())
                .build();
    }

    public List<MissionResultResponse> getMissionHistory(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("No travel plans found."));

        List<MissionReport> reports = missionReportRepository.findByTravelPlan(plan);

        return reports.stream().map(report -> MissionResultResponse.builder()
                .result(report.getResult())
                .imageUrl(report.getImageUrl())
                .build()).toList();
    }

    // Travel Start → Status: ONGOING
    public void startMission(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("No travel plans found."));

        if (plan.getStartDate() == null) {
            plan.setStartDate(LocalDate.now());
        }

        plan.setStatus(TravelStatus.ONGOING);
        travelPlanRepository.save(plan);
    }

    // Travel completed → Status: COMPLETED
    public void completeMission(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("No travel plans found."));

        plan.setStatus(TravelStatus.COMPLETED);
        travelPlanRepository.save(plan);

        if (travelReportRepository.findByTravelPlan(plan).isEmpty()) {
            CarbonEstimateRequest request = CarbonEstimateRequest.builder()
                    .planId(planId)
                    .build();
            TravelReportResponseDto reportDto = aiClient.getCarbonEstimate(request);

            reportService.saveReport(reportDto);
        }
    }

    // Status Inquiry → Reuse by Controller
    public List<FavoriteTravelResponse> getPlansByStatus(TravelStatus status, Long userId) {
        List<TravelPlan> plans = travelPlanRepository.findByStatusAndUserId(status, userId);
        if (plans == null || plans.isEmpty()) {
            return List.of();
        }

        return plans.stream()
                .filter(plan -> plan != null)
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

    // Add Mission Location Coordinate Inquiry API by Date
    public List<MissionLocationResponseDto> getMissionLocations(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("No travel plans found."));

        return plan.getScheduleList().stream()
                .filter(schedule -> schedule.getPlaces() != null && !schedule.getPlaces().isEmpty())
                .map(schedule -> {
                    TravelPlace firstPlace = schedule.getPlaces().get(0);

                    return MissionLocationResponseDto.builder()
                            .day(schedule.getDay())
                            .placeName(firstPlace.getName())
                            .latitude(firstPlace.getLatitude())
                            .longitude(firstPlace.getLongitude())
                            .build();
                })
                .toList();
    }

    // Check daily mission dates and certification status
    public TodayMissionStatusResponseDto getTodayMissionStatus(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("No travel plans found."));

        // Exception occurs if start date is null
        if (plan.getStartDate() == null) {
            throw new IllegalStateException("No travel start date has been set.");
        }

        // Calculate how many days today is (e.g. DAY 1 → Day 1)
        int dayNumber = (int) ChronoUnit.DAYS.between(plan.getStartDate(), LocalDate.now()) + 1;


        MissionReport latest = missionReportRepository
                .findTopByTravelPlanAndDayOrderByVerifiedAtDesc(plan, dayNumber);

        boolean isCompleted = (latest != null && "success".equalsIgnoreCase(latest.getResult()));

        return TodayMissionStatusResponseDto.builder()
                .day(dayNumber)
                .isCompleted(isCompleted)
                .build();
    }

    // Daily Mission Certification Contents
    public TodayMissionContentResponseDto getTodayMissionContent(Long planId) {
        TravelPlan plan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("No travel plans found."));

        int dayNumber = (int) ChronoUnit.DAYS.between(plan.getStartDate(), LocalDate.now()) + 1;

        if (dayNumber == 1) {
            return TodayMissionContentResponseDto.builder()
                    .day(1)
                    .placeName("Dol Hareubang")
                    .description("Today's Mission: Take a proof shot at the Dol Hareubang!")
                    .build();
        }

        TravelSchedule todaySchedule = plan.getScheduleList().stream()
                .filter(s -> s.getDay() == dayNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no schedule for that date."));

        TravelPlace mainPlace = todaySchedule.getPlaces().get(0);
        String placeName = mainPlace.getName();

        return TodayMissionContentResponseDto.builder()
                .day(dayNumber)
                .placeName(placeName)
                .description("Today's Mission: Take a proof shot at the" + placeName + "!")
                .build();
    }
}