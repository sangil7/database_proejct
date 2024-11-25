package com.tripmate.sns.dto;

import com.tripmate.sns.domain.Sns;
import lombok.Data;

@Data
public class SnsResponseDTO {

    private Long id;
    private String location;
    private String subLocation;
    private String photoUrl;

    // 엔티티를 DTO로 변환하는 메서드
    public static SnsResponseDTO fromEntity(Sns sns) {
        SnsResponseDTO responseDTO = new SnsResponseDTO();
        responseDTO.setId(sns.getId());
        responseDTO.setLocation(sns.getLocation());
        responseDTO.setSubLocation(sns.getSubLocation());
        responseDTO.setPhotoUrl(sns.getPhotoUrl());
        return responseDTO;
    }
}
