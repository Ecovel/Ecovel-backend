package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.FavoriteTravel;
import com.example.ecovel_server.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface FavoriteTravelRepository extends JpaRepository<FavoriteTravel, Long> {

    //특정 TravelPlan이 즐겨찾기된 적이 있는지 찾기 (중복 방지용)
    Optional<FavoriteTravel> findByTravelPlan(TravelPlan plan);

    // 모든 즐겨찾기된 여행 목록 반환
    //List<FavoriteTravel> findAll();

    //사용자 기반 조회 메서드 추가
    List<FavoriteTravel> findByUserId(Long userId);

    Optional<FavoriteTravel> findByUserIdAndTravelPlanId(Long userId, Long planId);

}
