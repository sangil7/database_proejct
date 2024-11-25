package com.tripmate.survey.domain;

public enum PreferredGender {
    MALE("남성"),
    FEMALE("여성"),
    OTHER("기타");

    private final String label;

    PreferredGender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
