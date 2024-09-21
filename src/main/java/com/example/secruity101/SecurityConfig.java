package com.example.secruity101;

import com.example.secruity101.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //설정 클래스다 임마
@EnableWebSecurity // 보안 설정
public class SecurityConfig {
    private final UserDetailService userDetailService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(UserDetailService userDetailService, JwtUtil jwtUtil) {
        this.userDetailService = userDetailService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtil, userDetailService);  // 여기서 필터 인스턴스를 생성

        //클라이언트 <-> 서버: http
        http
            .authorizeHttpRequests(authorize -> authorize
                    // /car/** 모두 car로 시작하는 api 허용가능
                    .requestMatchers("/auth/login", "/auth/signup", "/car/**").permitAll()  // 인증 없이 접근 가능
                    .anyRequest().authenticated()  //그 외의 요청은 인증 필요함 ㅅㄱ
            )
            .csrf(csrf -> csrf.disable()) //(cross-site request forgery) 같은 도메인이 아니면 막아주는 도구
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
