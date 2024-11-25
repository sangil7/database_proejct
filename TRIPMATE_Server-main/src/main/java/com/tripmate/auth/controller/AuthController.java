package com.tripmate.auth.controller;

import com.tripmate.auth.dto.KakaoUserDTO;
import com.tripmate.auth.service.AuthService;
import com.tripmate.auth.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 카카오 로그인 콜백 엔드포인트
    @CrossOrigin(origins = "http://localhost:3000")  // CORS 허용
    @Operation(summary = "카카오 로그인 콜백", description = "카카오 로그인 처리 후 사용자 정보를 DB에 저장하고 JWT 토큰을 발급.")
    @GetMapping("/api/auth/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoLoginCallback(@RequestParam String code) {
        KakaoUserDTO userInfo = authService.processKakaoLogin(code); // 카카오 로그인 처리
        Map<String, String> response = new HashMap<>();

        if (userInfo != null) {
            String token = authService.generateToken(userInfo); // JWT 토큰 생성
            response.put("token", token);  // 프론트엔드로 토큰 전달

            /**
             * 설문조사 여부도 체크, 또한 진행 중인 채팅이 있는지 확인해야 함
             * 진행 중인 채팅이 있을 경우 바로 채팅 페이지로 이동하게 해야 함
             */

            String userId = JwtUtil.getUserIdFromToken(); // JWT에서 userId 추출

            // 프로필 작성 여부 확인
            if (authService.isProfileComplete(userId)) {
                response.put("nextStep", "home"); // 프로필 완성 시 홈 화면으로 이동
            } else {
                response.put("nextStep", "profile"); // 프로필 미완성 시 프로필 작성 화면으로 이동
            }
            return ResponseEntity.ok(response);  // 상태 정보를 프론트엔드에 응답으로 전달
        } else {
            // 로그인 실패 시 오류 메시지와 상태 전달
            response.put("error", "로그인 실패");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/api/auth/temporary-login")
    public ResponseEntity<Map<String, String>> temporaryLogin() {
        String token = authService.temporaryLogin(); // 임시 로그인 메서드 호출
        Map<String, String> response = new HashMap<>();
        response.put("token", token); // JWT 토큰을 JSON 응답으로 반환
        return ResponseEntity.ok(response);
    }
}
