package com.example.ecovel_server.repository;

import com.example.ecovel_server.entity.QuizAnswerLog;
import com.example.ecovel_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuizAnswerLogRepository extends JpaRepository<QuizAnswerLog, Long> {

    List<QuizAnswerLog> findAllByUserAndDate(User user, LocalDate date);
}