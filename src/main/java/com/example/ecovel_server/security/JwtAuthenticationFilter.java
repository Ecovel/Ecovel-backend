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

    // List of endpoints to exclude JWT scans
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
            "/hello",
            "/travel/districts",
            "/travel/options"
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

        // GET /posts/** requests are excluded from going through the JWT filter
        if (request.getMethod().equalsIgnoreCase("GET") && path.startsWith("/posts")) {
            return true;
        }

        // Exclude filters even if they are included in a list of URL's for handling certain exceptions
        return EXCLUDED_ENDPOINTS.contains(path) || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")|| path.startsWith("/hello");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        System.out.println("Incoming Request URI: " + requestURI); // Add Request URI Logs

        // Verify that you are an endpoint to handle exceptions
        if (EXCLUDED_ENDPOINTS.contains(requestURI) || requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v3/api-docs")) {
            System.out.println("Skipping JWT filter for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }


        try {
            String token = getTokenFromRequest(request);
            System.out.println("Extracted Token: " + token);

            if (token != null && jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                System.out.println("Extracted Username from JWT: " + email);

                UserDetails userDetails = getUserService().loadUserByUsername(email);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("User authenticated: " + email);
                }
            } else {
                System.out.println("Invalid JWT Token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token has expired");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
            return;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            System.out.println("JWT Authentication failed");
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
