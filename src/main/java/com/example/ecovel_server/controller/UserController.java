package com.example.ecovel_server.controller;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.entity.User;
import com.example.ecovel_server.service.UserService;
import com.example.ecovel_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody UserRequest request) {
        try {
            String message = userService.signup(request);
            return ApiResponse.success(message);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody UserLoginRequest request) {
        try {
            String token = userService.login(request);
            return ApiResponse.success(new AuthResponse(token));
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/check-email")
    public ApiResponse<String> checkEmail(@RequestParam String email) {
        boolean exists = userService.checkEmail(email);
        if (exists) {
            return ApiResponse.error("이미 있는 이메일입니다.");
        } else {
            return ApiResponse.success("사용 가능한 이메일입니다.");
        }
    }

    @GetMapping("/check-nickname")
    public ApiResponse<String> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.checkNickname(nickname);
        if (exists) {
            return ApiResponse.error("이미 있는 닉네임입니다.");
        } else {
            return ApiResponse.success("사용 가능한 닉네임입니다.");
        }
    }

    // userId 조회
    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email);

            UserInfoResponse userInfo = new UserInfoResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getPhonenumber()
            );

            return ApiResponse.success(userInfo);
        } catch (Exception e) {
            return ApiResponse.error("유저 정보 조회 실패: " + e.getMessage());
        }
    }
}