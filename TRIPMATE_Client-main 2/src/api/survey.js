import axiosInstance from "./base";

// 설문조사 제출 함수
export const submitSurvey = async (surveyData) => {
  const token = localStorage.getItem("jwtToken"); // JWT 토큰 가져오기
  const orderedData = {
    preferredAge: surveyData.preferredAge,
    preferredGender: surveyData.preferredGender,
    culturalTourism: surveyData.culturalTourism,
    shopping: surveyData.shopping,
    natureTourism: surveyData.natureTourism,
    leisureSports: surveyData.leisureSports,
    historicalTourism: surveyData.historicalTourism,
    food: surveyData.food,
  };

  try {
    const response = await axiosInstance.post(
      "/api/survey/submit",
      orderedData,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`, // JWT 토큰 추가
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error(
      "Survey submission failed:",
      error.response ? error.response.data : error.message
    );
    throw error;
  }
};
