package com.example.ecovel_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DailyQuizDto {
    private String question;
    private String date;
    private boolean answer;
    private String explanation;
}

