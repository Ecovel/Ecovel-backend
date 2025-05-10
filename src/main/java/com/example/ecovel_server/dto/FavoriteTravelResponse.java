package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FavoriteTravelResponse {
    private Long favoriteId;
    private Long planId;
    private String city;
    private String district;
    private String style;
    private String duration;
    private List<String> transport;
    //private String imageUrl; // 첫 장소 이미지 URL
}
