package com.example.secruity101;

import com.example.secruity101.model.UserDetailCustom;
import com.example.secruity101.service.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;  // JWT 관련 유틸리티 클래스
    private final UserDetailService userDetailService;  // 사용자 정보를 불러오는 서비스

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailService userDetailService) {
        this.jwtUtil = jwtUtil;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Authorization 헤더에서 JWT 추출
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 2. 헤더에 "Bearer "로 시작하는 토큰이 있으면 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);  // 토큰에서 사용자 이름 추출
        }

        // 3. SecurityContext에 인증 정보가 없고, 토큰이 유효한 경우
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 4. 사용자 정보를 불러옴
            UserDetailCustom userDetails = userDetailService.loadUserByUsername(username);

            // 5. 토큰이 유효한지 검증
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                // 6. 인증 설정 (UsernamePasswordAuthenticationToken을 사용하여 인증 정보 생성)
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // 8. 필터 체인에 요청 전달
        filterChain.doFilter(request, response);
    }
}