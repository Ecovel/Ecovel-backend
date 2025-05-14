package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.GrowthLog;
import com.example.ecovel_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrowthLogRepository extends JpaRepository<GrowthLog, Long> {

    Optional<GrowthLog> findByUser(User user);
}