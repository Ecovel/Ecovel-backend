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

    private String question;

    private boolean answerTrue;  // true = O(참), false = X(거짓)

    private String date;         // (ex: "2025-05-13")

    @Column(length = 1000)
    private String explanation; // an explanation of the answer
}
