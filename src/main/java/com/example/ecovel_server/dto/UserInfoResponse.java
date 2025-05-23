package com.example.ecovel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String email;
    private String nickname;
    private String phonenumber;
}