package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuizSubmitResponseDto {
    private boolean correct;
    private String explanation;
}