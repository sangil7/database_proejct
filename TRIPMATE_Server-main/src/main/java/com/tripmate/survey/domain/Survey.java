package com.tripmate.survey.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;  // 사용자의 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferredAge preferredAge;  // 선호하는 동행자의 나이대

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferredGender preferredGender;  // 선호하는 동행자의 성별

    // 각 여행 스타일에 대한 점수 (1점~5점 사이)
    @Column(nullable = false)
    private int culturalTourism;  // 문화 관광 점수

    @Column(nullable = false)
    private int shopping;         // 쇼핑 점수

    @Column(nullable = false)
    private int natureTourism;    // 자연 관광 점수

    @Column(nullable = false)
    private int leisureSports;    // 레저 스포츠 점수

    @Column(nullable = false)
    private int historicalTourism;// 역사 관광 점수

    @Column(nullable = false)
    private int food;             // 음식 점수
}
