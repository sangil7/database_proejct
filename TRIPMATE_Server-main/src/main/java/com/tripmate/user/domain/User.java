package com.tripmate.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;  // 카카오에서 가져오는 사용자 ID

    @Column(nullable = false)
    private String nickname;

    private String profileImage;
    private String gender;
    private int age;
    private String mbti;
    private String introduction;

    @Embedded
    private Location location;

    // Embedded 클래스로 Location 정보 분리
    @Getter
    @Setter
    @NoArgsConstructor
    @Embeddable
    public static class Location {
        private String country;
        private String region;
    }
}
