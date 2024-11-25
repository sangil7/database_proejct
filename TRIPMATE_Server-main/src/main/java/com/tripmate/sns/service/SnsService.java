package com.tripmate.sns.service;

import com.tripmate.sns.domain.Sns;
import com.tripmate.sns.dto.SnsRequestDTO;
import com.tripmate.sns.dto.SnsResponseDTO;
import com.tripmate.sns.repository.SnsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnsService {

    private final SnsRepository snsRepository;
    private final S3Service s3Service;

    public SnsService(SnsRepository snsRepository, S3Service s3Service) {
        this.snsRepository = snsRepository;
        this.s3Service = s3Service;
    }

    // 게시글 생성
    public SnsResponseDTO createPost(SnsRequestDTO snsRequestDTO) throws IOException {
        MultipartFile photo = snsRequestDTO.getPhoto();
        String photoUrl = s3Service.uploadFile(photo);

        if (photoUrl == null || photoUrl.isEmpty()) {
            throw new RuntimeException("Failed to upload photo to S3.");
        }

        Sns sns = setSns(snsRequestDTO, photoUrl);
        sns = snsRepository.save(sns);
        return SnsResponseDTO.fromEntity(sns);
    }

    public Sns setSns(SnsRequestDTO snsRequestDTO, String photoUrl) {
        Sns sns = new Sns();
        sns.setLocation(snsRequestDTO.getLocation());
        sns.setSubLocation(snsRequestDTO.getSubLocation());
        sns.setPhotoUrl(photoUrl);
        return sns;
    }

    // 위치별 게시글 조회
    public List<SnsResponseDTO> getPostsByLocation(String location) {
        List<Sns> posts = snsRepository.findAllByLocation(location);
        return posts.stream()
                .map(SnsResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
