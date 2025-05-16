package com.example.ecovel_server.service;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.entity.*;
import com.example.ecovel_server.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelPlanRepository planRepo;
    private final TravelScheduleRepository scheduleRepo;
    private final TravelPlaceRepository placeRepo;

    private final UserRepository userRepo;
    private final RestTemplate restTemplate; //AI 서버에 HTTP POST 요청을 보내기 위한 스프링 클래스

    private final AIClient aiClient;

    @Transactional
    public TravelRecommendResponse recommendTravelPlan(TravelRecommendRequest req, Long userId) {

        String resolvedDistrict = resolveDistrict(req.getCity(), req.getDistrict());

        // 1. Request conversion
        TravelAIRequest aiReq = new TravelAIRequest();
        aiReq.setCity(req.getCity());
        aiReq.setDistrict(req.getDistrict());
        aiReq.setDuration(req.getDuration());
        aiReq.setStyle(req.getStyle());
        aiReq.setTransport(req.getTransport());

        // 2. AI call
        TravelAIResponse aiRes = aiClient.getRecommendation(aiReq);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("No User Information"));

        // 4. Save DB
        TravelPlan plan = planRepo.save(
                TravelPlan.builder()
                        .city(req.getCity())
                        .district(resolvedDistrict)
                        .duration(req.getDuration())
                        .style(req.getStyle())
                        .transport(req.getTransport())
                        .user(user)
                        .build()
        );

        //TravelScheduleDto
        List<TravelScheduleDto> scheduleDtoList = new ArrayList<>();

        for (TravelAIResponse.TravelAIDay aiDay : aiRes.getScheduleList()) {
            TravelSchedule schedule = scheduleRepo.save(
                    TravelSchedule.builder()
                            .day(Integer.parseInt(aiDay.getDay().replaceAll("[^0-9]", "")))
                            .travelPlan(plan)
                            .build()
            );

            //TravelPlaceDto
            List<TravelPlaceDto> placeDtos = new ArrayList<>();
            for (TravelAIResponse.TravelAIPlace aiPlace : aiDay.getPlaces()) {
                TravelPlace place = placeRepo.save(
                        TravelPlace.builder()
                                .name(aiPlace.getName())
                                .imageUrl(aiPlace.getImageUrl())
                                .walkTime(aiPlace.getWalkTime())
                                .bicycleTime(aiPlace.getBicycleTime())
                                .publicTime(aiPlace.getPublicTime())
                                .latitude(aiPlace.getLatitude())
                                .longitude(aiPlace.getLongitude())
                                .carTime(aiPlace.getCarTime())
                                .schedule(schedule)
                                .build()
                );

                placeDtos.add(TravelPlaceDto.builder()
                        .name(place.getName())
                        .imageUrl(place.getImageUrl())
                        .walkTime(place.getWalkTime())
                        .bicycleTime(place.getBicycleTime())
                        .publicTime(place.getPublicTime())
                        .carTime(place.getCarTime())
                        .build());
            }

            scheduleDtoList.add(TravelScheduleDto.builder()
                    .day(schedule.getDay())
                    .places(placeDtos)
                    .build());
        }

        // 5. Response DTO Configuration
        return TravelRecommendResponse.builder()
                .planId(plan.getId())
                .city(plan.getCity())
                .district(plan.getDistrict())
                .duration(plan.getDuration())
                .style(plan.getStyle())
                .transport(plan.getTransport())
                .scheduleList(scheduleDtoList)
                .build();
    }

    // When you select a phrase, choose a random phrase
    private String resolveDistrict(String city, String district) {
        if (district == null || district.equalsIgnoreCase("Random")) {
            List<String> candidates = getDistrictsByCity(city).getDistricts();
            if (!candidates.isEmpty()) {
                return candidates.get(new Random().nextInt(candidates.size()));
            }
        }
        return district;
    }

    // Travel Details Inquiry Method
    public TravelRecommendResponse getTravelPlanDetails(Long planId, Long userId) {
        TravelPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid travel plan ID"));

        List<TravelSchedule> schedules = scheduleRepo.findByTravelPlan(plan);
        List<TravelScheduleDto> scheduleDtoList = new ArrayList<>();

        for (TravelSchedule schedule : schedules) {
            List<TravelPlaceDto> placeDtos = new ArrayList<>();
            for (TravelPlace place : placeRepo.findBySchedule(schedule)) {
                placeDtos.add(TravelPlaceDto.builder()
                        .name(place.getName())
                        .imageUrl(place.getImageUrl())
                        .walkTime(place.getWalkTime())
                        .bicycleTime(place.getBicycleTime())
                        .publicTime(place.getPublicTime())
                        .carTime(place.getCarTime())
                        .build());
            }

            scheduleDtoList.add(TravelScheduleDto.builder()
                    .day(schedule.getDay())
                    .places(placeDtos)
                    .build());
        }

        boolean isFavorite = favoriteRepo.findByUserIdAndTravelPlanId(userId, planId).isPresent();

        return TravelRecommendResponse.builder()
                .planId(plan.getId())
                .city(plan.getCity())
                .district(plan.getDistrict())
                .duration(plan.getDuration())
                .style(plan.getStyle())
                .isFavorite(isFavorite)
                .transport(plan.getTransport())
                .scheduleList(scheduleDtoList)
                .build();
    }



    public TravelOptionResponse getTravelOptions() {
        return new TravelOptionResponse(
                List.of("Day Trip", "Overnight Trip", "3-Day Trip", "Extended Trip"),
                List.of("Nature", "Activity", "Culture", "Historic Sites"),
                List.of("Vehicle", "Bicycle", "Public Transport", "Walking")
        );
    }

    public DistrictResponse getDistrictsByCity(String city) {
        List<String> districts;

        switch (city.toLowerCase()) {
            case "seoul" -> districts = List.of("Jongno-gu", "Jung-gu", "Yongsan-gu", "Seongdong-gu", "Gwangjin-gu", "Dongdaemun-gu", "Jungnang-gu", "Seongbuk-gu", "Gangbuk-gu", "Dobong-gu", "Nowon-gu", "Eunpyeong-gu", "Seodaemun-gu", "Mapo-gu", "Yangcheon-gu", "Gangseo-gu", "Guro-gu", "Geumcheon-gu", "Yeongdeungpo-gu", "Dongjak-gu", "Gwanak-gu", "Seocho-gu", "Gangnam-gu", "Songpa-gu", "Gangdong-gu");
            case "busan" -> districts = List.of( "Jung-gu", "Seo-gu", "Dong-gu", "Yeongdo-gu", "Busanjin-gu", "Dongnae-gu", "Nam-gu", "Buk-gu", "Haeundae-gu", "Saha-gu", "Geumjeong-gu", "Gangseo-gu", "Yeonje-gu", "Suyeong-gu", "Sasang-gu", "Gijang-gun");
            case "daegu" -> districts = List.of("Jung-gu", "Dong-gu", "Seo-gu", "Nam-gu", "Buk-gu", "Suseong-gu", "Dalseo-gu", "Dalseong-gun");
            case "incheon" -> districts = List.of( "Jung-gu", "Dong-gu", "Michuhol-gu", "Yeonsu-gu", "Namdong-gu", "Bupyeong-gu", "Gyeyang-gu", "Seo-gu", "Ganghwa-gun", "Ongjin-gun");
            case "gwangju" -> districts = List.of("Dong-gu", "Seo-gu", "Nam-gu", "Buk-gu", "Gwangsan-gu");
            case "daejeon" -> districts = List.of( "Dong-gu", "Jung-gu", "Seo-gu", "Yuseong-gu", "Daedeok-gu");
            case "ulsan" -> districts = List.of(  "Jung-gu", "Nam-gu", "Dong-gu", "Buk-gu", "Ulju-gun");
            case "sejong" -> districts = List.of("Sejong-si");
            case "gyeonggi" -> districts = List.of( "Suwon-si", "Seongnam-si", "Uijeongbu-si", "Anyang-si", "Bucheon-si", "Gwangmyeong-si", "Pyeongtaek-si", "Dongducheon-si", "Ansan-si", "Goyang-si", "Gwacheon-si", "Guri-si", "Namyangju-si", "Osan-si", "Siheung-si", "Gunpo-si", "Uiwang-si", "Hanam-si", "Yongin-si", "Paju-si", "Icheon-si", "Anseong-si", "Gimpo-si", "Hwaseong-si", "Gwangju-si", "Yangju-si", "Pocheon-si", "Yeoju-si", "Yeoncheon-gun", "Gapyeong-gun", "Yangpyeong-gun");
            case "gangwon" -> districts = List.of(  "Chuncheon-si", "Wonju-si", "Gangneung-si", "Donghae-si", "Taebaek-si", "Sokcho-si", "Samcheok-si", "Hongcheon-gun", "Hoengseong-gun", "Yeongwol-gun", "Pyeongchang-gun", "Jeongseon-gun", "Cheorwon-gun", "Hwacheon-gun", "Yanggu-gun", "Inje-gun", "Goseong-gun", "Yangyang-gun");
            case "chungbuk" -> districts = List.of( "Cheongju-si", "Chungju-si", "Jecheon-si", "Boeun-gun", "Okcheon-gun", "Yeongdong-gun", "Jincheon-gun", "Goesan-gun", "Eumseong-gun", "Danyang-gun");
            case "chungnam" -> districts = List.of( "Cheonan-si", "Gongju-si", "Dangjin-si", "Boryeong-si", "Asan-si", "Seosan-si", "Nonsan-si", "Gyeryong-si", "Geumsan-gun", "Buyeo-gun", "Seocheon-gun", "Cheongyang-gun", "Hongseong-gun", "Yesan-gun", "Taean-gun");
            case "jeonbuk" -> districts = List.of("Jeonju-si", "Gunsan-si", "Iksan-si", "Jeongeup-si", "Namwon-si", "Gimje-si", "Wanju-gun", "Jinan-gun", "Muju-gun", "Jangsu-gun", "Imsil-gun", "Sunchang-gun", "Gochang-gun", "Buan-gun");
            case "jeonnam" -> districts = List.of(    "Mokpo-si", "Yeosu-si", "Suncheon-si", "Naju-si", "Gwangyang-si", "Damyang-gun", "Gokseong-gun", "Gurye-gun", "Goheung-gun", "Boseong-gun", "Hwasun-gun", "Jangheung-gun", "Gangjin-gun", "Haenam-gun", "Yeongam-gun", "Muan-gun", "Hampyeong-gun", "Yeonggwang-gun", "Jangseong-gun", "Wando-gun", "Jindo-gun", "Shinan-gun");
            case "gyeongbuk" -> districts = List.of( "Pohang-si", "Gyeongju-si", "Gimcheon-si", "Andong-si", "Gumi-si", "Yeongju-si", "Yeongcheon-si", "Sangju-si", "Mungyeong-si", "Gyeongsan-si", "Uiseong-gun", "Cheongsong-gun", "Yeongyang-gun", "Yeongdeok-gun", "Cheongdo-gun", "Goryeong-gun", "Seongju-gun", "Chilgok-gun", "Yecheon-gun", "Bonghwa-gun", "Uljin-gun", "Ulleung-gun");
            case "gyeongnam" -> districts = List.of(   "Changwon-si", "Jinju-si", "Tongyeong-si", "Sacheon-si", "Gimhae-si", "Miryang-si", "Geoje-si", "Yangsan-si", "Uiryeong-gun", "Haman-gun", "Changnyeong-gun", "Goseong-gun", "Namhae-gun", "Hadong-gun", "Sancheong-gun", "Hamyang-gun", "Geochang-gun", "Hapcheon-gun");
            case "jeju" -> districts = List.of("Jeju-si", "Seogwipo-si");
            default -> districts = List.of(); // 혹은 오류 반환
        }

        return new DistrictResponse(districts);
    }

    // Add Favorite Features
    @Autowired
    private FavoriteTravelRepository favoriteRepo;

    @Transactional
    public void addFavorite(Long planId, Long userId) {
        TravelPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new RuntimeException("I don't have a travel schedule."));

        if (favoriteRepo.findByTravelPlan(plan).isPresent())
            throw new RuntimeException("It's already a favorite.");

        plan.setStatus(TravelStatus.PLANNED);
        planRepo.save(plan);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("No User Information"));

        favoriteRepo.save(FavoriteTravel.builder()
                .travelPlan(plan)
                .user(user)
                .build());
    }

    @Transactional(readOnly = true)
    public List<FavoriteTravelResponse> getFavorites(Long userId) {
        return favoriteRepo.findByUserId(userId).stream().map(fav -> {
            TravelPlan plan = fav.getTravelPlan();


            return FavoriteTravelResponse.builder()
                    .favoriteId(fav.getId())
                    .planId(plan.getId())
                    .city(plan.getCity())
                    .district(plan.getDistrict())
                    .duration(plan.getDuration())
                    .style(plan.getStyle())
                    .transport(plan.getTransport())
                    .build();
        }).toList();
    }


    @Transactional
    public void deleteFavorite(Long favoriteId) {
        favoriteRepo.deleteById(favoriteId);
    }

    @Transactional
    public void updatePlanStatus(Long planId, TravelStatus status) {
        TravelPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("The itinerary does not exist."));
        plan.setStatus(status);
        planRepo.save(plan);
    }

    @Transactional(readOnly = true)
    public List<FavoriteTravelResponse> getTravelPlansByStatus(TravelStatus status) {
        List<TravelPlan> plans = planRepo.findByStatus(status);

        return plans.stream().map(plan -> {

            // Status-based queries do not require favorite IDs
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

}