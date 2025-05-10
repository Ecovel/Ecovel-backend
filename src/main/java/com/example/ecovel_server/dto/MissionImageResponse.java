package com.example.ecovel_server.dto;

//AI 서버 응답

import lombok.Data;

@Data
public class MissionImageResponse {
    private boolean success; //성공 여부
    private boolean placeMatch; //장소와 매치되는지
    private boolean faceMatch; //얼굴과 매치되는지
    private String message; //어떤 부분이 틀렸는지
}