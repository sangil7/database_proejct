package com.tripmate.survey.controller;

import com.tripmate.survey.dto.SurveyDTO;
import com.tripmate.survey.service.SurveyService;
import com.tripmate.auth.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @Operation(summary = "설문조사 제출", description = "사용자가 설문조사를 제출하고, 데이터를 저장.")
    @PostMapping("/submit")
    public ResponseEntity<String> submitSurvey(@RequestBody SurveyDTO surveyDTO) {
        String userId = JwtUtil.getUserIdFromToken(); // JWT에서 userId 추출
        surveyService.saveSurvey(userId, surveyDTO);
        return ResponseEntity.status(201).body("설문조사가 성공적으로 제출되었습니다.");
    }
}
