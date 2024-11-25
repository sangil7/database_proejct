package com.tripmate.survey.dto;

import com.tripmate.survey.domain.PreferredAge;
import com.tripmate.survey.domain.PreferredGender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;




@Data
public class SurveyDTO {

    private PreferredAge preferredAge;
    private PreferredGender preferredGender;

    @Min(1) @Max(5)
    private int culturalTourism;

    @Min(1) @Max(5)
    private int shopping;

    @Min(1) @Max(5)
    private int natureTourism;

    @Min(1) @Max(5)
    private int leisureSports;

    @Min(1) @Max(5)
    private int historicalTourism;

    @Min(1) @Max(5)
    private int food;
}
