package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // 1. 오늘의 퀴즈 조회
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<DailyQuizDto>> getTodayQuiz() {
        try {
            return ResponseEntity.ok(ApiResponse.success(quizService.getTodayQuiz()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // 오늘의 퀴즈 정답 및 해설
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<QuizSubmitResponseDto>> submitQuiz(
            @RequestBody QuizSubmitRequestDto request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(quizService.submitQuiz(request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    //중복 응답 방지용
    @GetMapping("/answered")
    public ResponseEntity<ApiResponse<QuizAnsweredStatusDto>> hasAnsweredToday(@RequestParam Long userId) {
        try {
            boolean answered = quizService.hasAnsweredToday(userId);
            return ResponseEntity.ok(ApiResponse.success(
                    QuizAnsweredStatusDto.builder().answered(answered).build()
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}