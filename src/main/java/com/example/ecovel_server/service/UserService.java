package com.example.ecovel_server.service;

import com.example.ecovel_server.dto.*;
import com.example.ecovel_server.entity.User;
import com.example.ecovel_server.repository.UserRepository;
import com.example.ecovel_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String signup(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("이메일 중복");
        if (userRepository.existsByNickname(request.getNickname()))
            throw new RuntimeException("닉네임 중복");

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phonenumber(request.getPhonenumber())
                .build();

        userRepository.save(user);
        return "회원가입 완료";
    }

    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 존재 안 함"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("비밀번호 오류");

        return jwtUtil.generateToken(user.getEmail());
    }

    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저 없음"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), List.of()
        );
    }

    //사용X
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("이메일에 해당하는 사용자 없음"));
    }
}
