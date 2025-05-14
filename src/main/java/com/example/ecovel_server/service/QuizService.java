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
                    .answerTrue(aiQuiz.isAnswer())  // ✔ AI가 answer 전달
                    .explanation(aiQuiz.getExplanation())
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

        // 중복 제출 여부 확인 - List로 변경
        if (!quizAnswerLogRepository.findAllByUserAndDate(user, today).isEmpty()) {
            throw new IllegalStateException("이미 제출됨");
        }

        DailyQuiz quiz = dailyQuizRepository.findByDate(today.toString())
                .orElseThrow(() -> new IllegalStateException("오늘 퀴즈 없음"));

        boolean correct = (quiz.isAnswerTrue() == request.isAnswer());

        // 기록 저장
        quizAnswerLogRepository.save(
                QuizAnswerLog.builder()
                        .user(user)
                        .date(today)
                        .isCorrect(correct)
                        .build()
        );

        // 성장 로그 반영
        if (correct) {
            growthService.updateGrowthLogAfterQuizSuccess(user.getId());
        }

        // 해설 저장
        return QuizSubmitResponseDto.builder()
                .correct(correct)
                .explanation(quiz.getExplanation())
                .build();
    }

    // 오늘 퀴즈 제출 여부 확인
    public QuizAnsweredStatusDto getAnsweredStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();
        List<QuizAnswerLog> logs = quizAnswerLogRepository.findAllByUserAndDate(user, today);

        if (!logs.isEmpty()) {
            QuizAnswerLog log = logs.get(0); // 가장 첫 번째 응답만 사용
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