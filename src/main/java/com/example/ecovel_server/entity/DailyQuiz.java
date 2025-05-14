package com.example.ecovel_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;     // 퀴즈 질문

    private boolean answerTrue;  // 정답: true = O(참), false = X(거짓)

    private String date;         // 퀴즈 날짜 (예: "2025-05-13")

    @Column(length = 1000)
    private String explanation; // 정답 해설
}
