package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.FavoriteTravel;
import com.example.ecovel_server.entity.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface FavoriteTravelRepository extends JpaRepository<FavoriteTravel, Long> {

    //Find if a particular TravelPlan has ever been a favorite (to prevent duplication)
    Optional<FavoriteTravel> findByTravelPlan(TravelPlan plan);

    //Add user-based query methods
    List<FavoriteTravel> findByUserId(Long userId);

    Optional<FavoriteTravel> findByUserIdAndTravelPlanId(Long userId, Long planId);

}
