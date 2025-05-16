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
import java.util.List;

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
                    .answerTrue(aiQuiz.isAnswer())
                    .explanation(aiQuiz.getExplanation())
                    .build();

            return dailyQuizRepository.save(newQuiz);
        });

        return DailyQuizDto.builder()
                .question(quiz.getQuestion())
                .date(quiz.getDate())
                .answer(quiz.isAnswerTrue())
                .explanation(quiz.getExplanation())
                .build();
    }

    @Transactional
    public QuizSubmitResponseDto submitQuiz(QuizSubmitRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No User"));

        LocalDate today = LocalDate.now();

        // Check duplicate submissions
        if (!quizAnswerLogRepository.findAllByUserAndDate(user, today).isEmpty()) {
            throw new IllegalStateException("Already submitted");
        }

        DailyQuiz quiz = dailyQuizRepository.findByDate(today.toString())
                .orElseThrow(() -> new IllegalStateException("No quiz today"));

        boolean correct = (quiz.isAnswerTrue() == request.isAnswer());

        // Save records
        quizAnswerLogRepository.save(
                QuizAnswerLog.builder()
                        .user(user)
                        .date(today)
                        .isCorrect(correct)
                        .build()
        );

        // Reflect growth log
        if (correct) {
            growthService.updateGrowthLogAfterQuizSuccess(user.getId());
        }

        return QuizSubmitResponseDto.builder()
                .correct(correct)
                .explanation(quiz.getExplanation())
                .build();
    }

    // Check if you submit a quiz today
    public QuizAnsweredStatusDto getAnsweredStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        LocalDate today = LocalDate.now();
        List<QuizAnswerLog> logs = quizAnswerLogRepository.findAllByUserAndDate(user, today);

        if (!logs.isEmpty()) {
            QuizAnswerLog log = logs.get(0);
            DailyQuiz quiz = dailyQuizRepository.findByDate(today.toString()).orElse(null);

            return QuizAnsweredStatusDto.builder()
                    .answered(true)
                    .isCorrect(log.isCorrect())
                    .explanation(quiz != null ? quiz.getExplanation() : null)
                    .build();
        }

        return QuizAnsweredStatusDto.builder().answered(false).build();
    }
}