package com.example.ecovel_server.security;

import com.example.ecovel_server.service.UserService;
import com.example.ecovel_server.util.JwtUtil; // 변경된 패키지 경로
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider; // ObjectProvider 사용
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectProvider<UserService> userServiceProvider;

    // JWT 검사를 제외할 엔드포인트 목록
    private static final List<String> EXCLUDED_ENDPOINTS = List.of(
            "/api/users/login",
            "/api/users/signup",
            "/api/users/check-email",
            "/api/users/check-nickname",
            "/api/ai/recommend",
            "/api/ai/image",
            "/api/ai/carbon/coefficients",
            "/api/ai/carbon/estimate",
            "/swagger-ui",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/hello"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ObjectProvider<UserService> userServiceProvider) {
        this.jwtUtil = jwtUtil;
        this.userServiceProvider = userServiceProvider;
    }

    private UserService getUserService() {
        return userServiceProvider.getIfAvailable();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // GET /posts/** 요청은 JWT 필터를 거치지 않도록 제외
        if (request.getMethod().equalsIgnoreCase("GET") && path.startsWith("/posts")) {
            return true;
        }

        // 특정 예외 처리 URL 리스트에 포함된 경우도 필터 제외
        return EXCLUDED_ENDPOINTS.contains(path) || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")|| path.startsWith("/hello");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        System.out.println("Incoming Request URI: " + requestURI); // ✅ 요청 URI 로그 추가

        // 예외 처리할 엔드포인트인지 확인
        if (EXCLUDED_ENDPOINTS.contains(requestURI) || requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v3/api-docs")) {
            System.out.println("Skipping JWT filter for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }


        try {
            String token = getTokenFromRequest(request);
            System.out.println("Extracted Token: " + token); // ✅ JWT 추출 로그

            if (token != null && jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                System.out.println("Extracted Username from JWT: " + email); // ✅ 사용자 이름 추출 로그

                UserDetails userDetails = getUserService().loadUserByUsername(email);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("User authenticated: " + email); // ✅ 인증 성공 로그
                }
            } else {
                System.out.println("Invalid JWT Token"); // ✅ JWT가 유효하지 않음
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token has expired"); // ✅ JWT 만료 로그
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
            return;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            System.out.println("JWT Authentication failed"); // ✅ 인증 실패 로그
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
