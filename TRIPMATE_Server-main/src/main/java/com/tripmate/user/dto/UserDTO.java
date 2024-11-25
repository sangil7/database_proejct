package com.tripmate.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDTO {

    private int age;

    private String gender;


    private String profileImage;

    private String mbti;
    private String introduction;
    private String locationCountry;
    private String locationRegion;

    // 기존 userId와 nickname은 DTO에서 제외
}
