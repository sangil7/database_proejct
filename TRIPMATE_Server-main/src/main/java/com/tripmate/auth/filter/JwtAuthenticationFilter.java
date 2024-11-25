package com.tripmate.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value; // 추가된 부분
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //임시 jwt 하드코딩 키
    @Value("${jwt.secret}") // yml에서 JWT 비밀 키를 읽어오는 부분
    private String jwtSecret; // 추가된 부분

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret)  // yml 파일에서 읽어온 비밀 키 사용
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                userId = claims.getSubject();
            } catch (Exception e) {
                logger.error("Invalid JWT token", e);
            }
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userId, null, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}*/
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        // Authorization 헤더 확인 로그 추가
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            logger.info("JWT Token found in request: {}", token);

            try {
                // 토큰을 파싱하고 검증하는 로그 추가
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                userId = claims.getSubject();
                logger.info("JWT Token parsed successfully. User ID: {}", userId);

            } catch (ExpiredJwtException e) {
                logger.error("JWT token expired", e);
            } catch (SignatureException e) {
                logger.error("JWT signature does not match locally computed signature", e);
            } catch (Exception e) {
                logger.error("Invalid JWT token", e);
            }
        } else {
            logger.warn("Authorization header is missing or does not start with Bearer");
        }

        // userId가 존재하고 SecurityContext에 인증이 설정되지 않았는지 확인하는 로그 추가
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userId, null, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("User authenticated successfully with user ID: {}", userId);
        } else if (userId == null) {
            logger.warn("User ID extraction failed, setting authentication to null.");
        }

        filterChain.doFilter(request, response);
    }
}
