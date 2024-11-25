package com.tripmate.user.controller;

import com.tripmate.user.dto.UserDTO;
import com.tripmate.user.service.UserService;
import com.tripmate.auth.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 프로필 정보를 조회하는 엔드포인트
    @Operation(summary = "사용자 프로필 조회", description = "사용자의 프로필 정보를 조회.")
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile() {
        String userId = JwtUtil.getUserIdFromToken(); // JWT에서 userId 추출
        UserDTO userProfile = userService.getUserProfile(userId);
        if (userProfile != null) {
            return ResponseEntity.ok(userProfile); // 프로필 정보 반환
        }
        return ResponseEntity.status(401).body(null); // 인증 실패
    }

    // 프로필 정보를 등록하는 엔드포인트
    @Operation(summary = "사용자 프로필 등록", description = "사용자가 입력한 프로필을 저장.")
    @PostMapping("/profile")
    public ResponseEntity<Map<String, String>> registerProfile(@RequestBody @Valid UserDTO userDTO) {
        String userId = JwtUtil.getUserIdFromToken(); // JWT에서 userId 추출
        Map<String, String> response = new HashMap<>();
        try {
            userService.registerUserProfile(userDTO, userId);

            // 성공적으로 프로필이 등록되었을 경우 프론트엔드로 전달할 정보
            response.put("status", "success");
            response.put("message", "프로필이 성공적으로 등록되었습니다.");
            response.put("nextStep", "survey");  // 프론트엔드에서 설문조사 페이지로 이동하도록 알림

            return ResponseEntity.ok(response);  // 프론트엔드로 상태와 정보 전달
        } catch (Exception e) {
            // 오류가 발생한 경우, 프론트엔드로 오류 메시지를 전달
            response.put("status", "error");
            response.put("message", "프로필 등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
