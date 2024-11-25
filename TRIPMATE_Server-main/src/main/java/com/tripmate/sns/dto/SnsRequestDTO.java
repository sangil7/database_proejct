package com.tripmate.sns.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SnsRequestDTO {
    private String location;
    private String subLocation;
    private MultipartFile photo;
}