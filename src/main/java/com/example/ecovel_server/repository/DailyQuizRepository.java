package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.DailyQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyQuizRepository extends JpaRepository<DailyQuiz, Long> {

    Optional<DailyQuiz> findByDate(String date); // 날짜별 퀴즈 조회
}

