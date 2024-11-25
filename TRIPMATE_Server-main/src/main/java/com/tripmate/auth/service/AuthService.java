package com.tripmate.auth.service;

import com.tripmate.auth.dto.KakaoUserDTO;
import com.tripmate.user.domain.User;
import com.tripmate.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

// 로그인 링크: https://accounts.kakao.com/login/?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fresponse_type%3Dcode%26client_id%3Df0ff8c31de4f0a05529065e27b305c48%26redirect_uri%3Dhttp%253A%252F%252Flocalhost%253A8080%252Fcallback%26through_account%3Dtrue#login

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.client_secret}")
    private String clientSecret;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Value("${kakao.token_uri}")
    private String tokenUri;

    @Value("${kakao.user_info_uri}")
    private String userInfoUri;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // 카카오 로그인 처리 후 사용자 정보 저장 로직
    public KakaoUserDTO processKakaoLogin(String code) {
        // 엑세스 토큰 발급 요청
        String accessToken = getAccessToken(code); // Access Token 발급
        if (accessToken != null) {
            // 카카오 사용자 정보 가져오기
            KakaoUserDTO userInfo = getUserInfoFromKakao(accessToken);

            // DB에 사용자 정보 저장
            Optional<User> userOptional = userRepository.findByUserId(userInfo.getId());
            if (userOptional.isEmpty()) {
                User newUser = new User();
                newUser.setUserId(userInfo.getId());
                newUser.setNickname(userInfo.getNickname());
                userRepository.save(newUser);
            }

            return userInfo;  // 사용자 정보 반환
        }
        return null;  // 토큰 발급 실패 시 null 반환
    }

    // JWT 토큰 생성
    public String generateToken(KakaoUserDTO userInfo) {
        Optional<User> userOptional = userRepository.findByUserId(userInfo.getId());
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found"));

        // JWT 토큰 생성
        return Jwts.builder()
                .setSubject(user.getUserId())
                .claim("nickname", user.getNickname())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // 1일간 유효
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    // 카카오 엑세스 토큰 발급 요청
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                return (String) responseBody.get("access_token");
            }
        }
        return null;  // 토큰 발급 실패 시 null 반환
    }

    // 사용자 정보 가져오기
    public KakaoUserDTO getUserInfoFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);  // Bearer 토큰 설정

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                String userId = responseBody.get("id").toString();
                Map<String, Object> properties = (Map<String, Object>) responseBody.get("properties");
                String nickname = (String) properties.get("nickname");

                return new KakaoUserDTO(userId, nickname); // 사용자 ID와 닉네임 반환
            }
        }
        return null;  // 사용자 정보 가져오기 실패 시 null 반환
    }

    // 프로필이 완성되었는지 확인하는 메서드 추가
    public boolean isProfileComplete(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 프로필의 나머지 필드들이 채워져 있는지 확인
            return user.getAge() != 0 && user.getGender() != null && user.getMbti() != null && user.getIntroduction() != null;
        }
        return false;
    }

    // 임시 로그인 메서드 (테스트용)
    public String temporaryLogin() {
        // 임시 유저 ID와 닉네임으로 KakaoUserDTO 생성
        KakaoUserDTO kakaoUser = new KakaoUserDTO("testUserId", "Test User");

        // User 객체 생성 및 정보 설정
        User user = new User();
        user.setUserId(kakaoUser.getId());
        user.setNickname(kakaoUser.getNickname());

        // DB에 사용자 정보 저장
        userRepository.save(user);

        // JWT 토큰 생성
        return generateToken(kakaoUser);
    }
}
