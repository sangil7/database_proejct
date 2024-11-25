package com.tripmate.survey.domain;

public enum PreferredAge {
    TEENS("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    FORTIES("40대"),
    FIFTIES("50대"),
    SIXTIES("60대"),
    SEVENTIES("70대");

    private final String label;

    PreferredAge(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
