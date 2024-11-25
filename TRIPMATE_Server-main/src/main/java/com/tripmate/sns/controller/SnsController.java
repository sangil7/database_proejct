package com.tripmate.sns.controller;

import com.tripmate.sns.dto.SnsRequestDTO;
import com.tripmate.sns.dto.SnsResponseDTO;
import com.tripmate.sns.service.SnsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sns")
public class SnsController {

    private final SnsService snsService;

    // 게시글 작성 API
    @Operation(summary = "게시글 작성", description = "사진을 저장")
    @PostMapping("/post")
    public ResponseEntity<String> createPost(@ModelAttribute SnsRequestDTO snsRequestDTO
    ) throws IOException {
        // 저장
        snsService.createPost(snsRequestDTO);

        log.info("location={}", snsRequestDTO.getLocation());
        log.info("subLocation={}", snsRequestDTO.getSubLocation());
        log.info("photo={}", snsRequestDTO.getPhoto());

        return ResponseEntity.ok("저장완료");
    }

    // 게시글 조회 API
    @Operation(summary = "게시글 조회", description = "위치에 해당하는 사진과 태그를 list 형식으로 전달")
    @GetMapping("/posts")
    public ResponseEntity<List<SnsResponseDTO>> getPostsByLocation(@RequestParam String location) {
        List<SnsResponseDTO> posts = snsService.getPostsByLocation(location);
        return ResponseEntity.ok(posts);
    }
}
