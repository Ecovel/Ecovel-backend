package com.example.ecovel_server.service;

import com.example.ecovel_server.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AIClient {

    private final RestTemplate restTemplate;

    @Value("${ai.server.recommend-url}")
    private String recommendUrl;

    @Value("${ai.server.carbon-url}")
    private String carbonEstimateUrl;

    @Value("${ai.server.verify-image-url}")
    private String verifyImageUrl;

    /** 여행 일정 추천 (기존) */
    public TravelAIResponse getRecommendation(TravelAIRequest request) {
        return restTemplate.postForObject(recommendUrl, request, TravelAIResponse.class);
    }

    /** 일정 기반 탄소 분석 요청 (신규) */
    public TravelReportResponseDto getCarbonEstimate(CarbonEstimateRequest request) {
        return restTemplate.postForObject(carbonEstimateUrl, request, TravelReportResponseDto.class);
    }

    //미션  인증
    public MissionImageResponse verifyMissionImage(MultiValueMap<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(verifyImageUrl, requestEntity, MissionImageResponse.class);
    }
}
