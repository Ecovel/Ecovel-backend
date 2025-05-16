package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuizAnsweredStatusDto {
    private boolean answered;
    private Boolean isCorrect;      // nullable allow
    private String explanation;     // nullable allow
}
