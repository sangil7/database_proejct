package com.tripmate.survey.service;

import com.tripmate.survey.domain.Survey;
import com.tripmate.survey.dto.SurveyDTO;

import com.tripmate.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public void saveSurvey(String userId, SurveyDTO surveyDTO) {
        Survey survey = new Survey();
        survey.setUserId(userId);
        survey.setPreferredAge(surveyDTO.getPreferredAge());
        survey.setPreferredGender(surveyDTO.getPreferredGender());
        survey.setCulturalTourism(surveyDTO.getCulturalTourism());
        survey.setShopping(surveyDTO.getShopping());
        survey.setNatureTourism(surveyDTO.getNatureTourism());
        survey.setLeisureSports(surveyDTO.getLeisureSports());
        survey.setHistoricalTourism(surveyDTO.getHistoricalTourism());
        survey.setFood(surveyDTO.getFood());

        surveyRepository.save(survey);
    }
}
