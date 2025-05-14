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

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<DailyQuizDto>> getTodayQuiz() {
        try {
            return ResponseEntity.ok(ApiResponse.success(quizService.getTodayQuiz()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<QuizSubmitResponseDto>> submitQuiz(
            @RequestBody QuizSubmitRequestDto request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(quizService.submitQuiz(request)));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/answered")
    public ResponseEntity<ApiResponse<QuizAnsweredStatusDto>> hasAnsweredToday(@RequestParam Long userId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(
                    quizService.getAnsweredStatus(userId)
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}