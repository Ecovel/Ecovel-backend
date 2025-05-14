package com.example.ecovel_server.service;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.entity.*;
import com.example.ecovel_server.repository.DailyQuizRepository;
import com.example.ecovel_server.repository.QuizAnswerLogRepository;
import com.example.ecovel_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final DailyQuizRepository dailyQuizRepository;
    private final QuizAnswerLogRepository quizAnswerLogRepository;
    private final UserRepository userRepository;
    private final GrowthService growthService;
    private final AIClient aiClient;

    // 오늘의 퀴즈 조회
    @Transactional
    public DailyQuizDto getTodayQuiz() {
        String today = LocalDate.now().toString();

        DailyQuiz quiz = dailyQuizRepository.findByDate(today).orElseGet(() -> {
            DailyQuizDto aiQuiz = aiClient.getTodayQuizFromAI();

            DailyQuiz newQuiz = DailyQuiz.builder()
                    .question(aiQuiz.getQuestion())
                    .date(today)
                    .answerTrue(false)
                    .explanation("")
                    .build();

            return dailyQuizRepository.save(newQuiz);
        });

        return DailyQuizDto.builder()
                .question(quiz.getQuestion())
                .date(quiz.getDate())
                .build();
    }

    @Transactional
    public QuizSubmitResponseDto submitQuiz(QuizSubmitRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        LocalDate today = LocalDate.now();

        if (quizAnswerLogRepository.findByUserAndDate(user, today).isPresent()) {
            throw new IllegalStateException("이미 제출됨");
        }

        DailyQuiz quiz = dailyQuizRepository.findByDate(today.toString())
                .orElseThrow(() -> new IllegalStateException("오늘 퀴즈 없음"));

        // AI에 채점 요청 (DTO 그대로 전송)
        QuizSubmitResponseDto aiResponse = aiClient.submitQuizToAI(request);

        // 기록 저장
        quizAnswerLogRepository.save(
                QuizAnswerLog.builder()
                        .user(user)
                        .date(today)
                        .isCorrect(aiResponse.isCorrect())
                        .build()
        );

        // 성장 로그 반영
        if (aiResponse.isCorrect()) {
            growthService.updateGrowthLogAfterQuizSuccess(user.getId());
        }

        // 해설 저장
        quiz.setExplanation(aiResponse.getExplanation());
        dailyQuizRepository.save(quiz);

        return aiResponse;
    }

    // 오늘 퀴즈 제출 여부 확인
    public boolean hasAnsweredToday(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return quizAnswerLogRepository.findByUserAndDate(user, LocalDate.now()).isPresent();
    }
}