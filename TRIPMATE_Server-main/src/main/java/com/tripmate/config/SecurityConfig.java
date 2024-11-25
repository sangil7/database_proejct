package com.tripmate.config;

import com.tripmate.auth.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // CSRF 보호 비활성화
                .cors().and()      // CORS 허용 추가
                .authorizeRequests()
                .requestMatchers("/api/auth/kakao/callback/","/login", "/callback", "/api/auth/kakao/**", "/api/auth/temporary-login", "/ws/chat").permitAll()  // 인증 필요 없는 경로에 /ws/chat 추가
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                .permitAll()  // Swagger 경로 허용
                .anyRequest().authenticated()  // 나머지 모든 요청은 인증 필요
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
