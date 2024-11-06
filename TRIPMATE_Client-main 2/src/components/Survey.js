import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { submitSurvey } from "../api/survey";
import Image from "./image/image1.jpg";

const AgeCategory = {
  TWENTIES: "TWENTIES",
  THIRTIES: "THIRTIES",
  FORTIES: "FORTIES",
  FIFTIES: "FIFTIES",
  SIXTIES_PLUS: "SIXTIES_PLUS",
};

const ageCategoryMapping = {
  1: AgeCategory.TWENTIES,
  2: AgeCategory.THIRTIES,
  3: AgeCategory.FORTIES,
  4: AgeCategory.FIFTIES,
  5: AgeCategory.SIXTIES_PLUS,
};

function Survey() {
  const navigate = useNavigate();
  const [step, setStep] = useState("1-page");
  const [request, setRequest] = useState({});
  const [token, setToken] = useState(localStorage.getItem("token")); // 토큰을 상태로 관리

  useEffect(() => {
    const handleStorageChange = () => {
      const newToken = localStorage.getItem("token");
      setToken(newToken);
      console.log("Updated Access Token:", newToken);
    };

    window.addEventListener("storage", handleStorageChange);

    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, []);

  const handleNext = async (step, data) => {
    const newRequest = { ...request, ...data };
    console.log("newRequest: ", newRequest);
    setRequest(newRequest);

    if (step === "final") {
      try {
        await submitSurvey(newRequest, token);
        navigate("/home");
      } catch (error) {
        console.error("Error submitting survey:", error);
      }
    } else {
      setStep(step);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6 max-w-1/2 z-10">
      <img
        src={Image}
        alt="설명"
        className="absolute inset-0 w-full h-full object-cover opacity-50 z-0"
      />
      <div className="bg-white rounded-lg shadow-lg p-8 w-full max-w-1/3 z-10">
        {step === "1-page" && (
          <Survey1Page onNext={(data) => handleNext("2-page", data)} />
        )}
        {step === "2-page" && (
          <Survey2Page onNext={(data) => handleNext("final", data)} />
        )}
      </div>
    </div>
  );
}
export default Survey;

function Survey1Page({ onNext }) {
  const [selected, setSelected] = useState({
    preferredGender: null,
    preferredAge: null,
  });

  const isDisabled =
    selected.preferredGender === null || selected.preferredAge === null;

  const handleSelected = (key, value) => {
    setSelected({ ...selected, [key]: value });
  };

  const handleNext = () => {
    const updatedData = {
      ...selected,
      preferredAge: ageCategoryMapping[selected.preferredAge], // 숫자 값에서 Enum 값으로 변환
    };
    onNext(updatedData);
  };

  const buttonStyle = (isSelected) => ({
    backgroundColor: isSelected ? "#3B82F6" : "#FFFFFF",
    color: isSelected ? "#FFFFFF" : "#3B82F6",
    border: `2px solid ${isSelected ? "#3B82F6" : "#D1D5DB"}`,
    padding: "6px 12px", // 버튼의 크기를 줄이기 위해 padding 값 조정
    fontSize: "14px", // 버튼의 텍스트 크기를 줄이기 위해 font-size 조정
  });

  return (
    <div className="bg-white bg-opacity-90 p-8 rounded-lg shadow-lg max-w-1/2 w-full">
      <h1 className="text-3xl font-bold text-center mb-6">VOYAGE CONNECT</h1>
      <h2 className="text-xl font-bold mb-4">| 설문 조사</h2>

      <div className="my-6 border-b border-white"></div>

      <div className="mb-6">
        <label className="block text-gray-700 mb-2">선호하는 동행자 성별</label>
        <div className="flex justify-around mt-2">
          <button
            style={buttonStyle(selected.preferredGender === "MALE")}
            className="py-2 px-4 rounded-md transition duration-200"
            onClick={() => handleSelected("preferredGender", "MALE")}
          >
            남성
          </button>
          <button
            style={buttonStyle(selected.preferredGender === "FEMALE")}
            className="py-2 px-4 rounded-md transition duration-200"
            onClick={() => handleSelected("preferredGender", "FEMALE")}
          >
            여성
          </button>
          <button
            style={buttonStyle(selected.preferredGender === "ANY")}
            className="py-2 px-4 rounded-md transition duration-200"
            onClick={() => handleSelected("preferredGender", "ANY")}
          >
            상관없음
          </button>
        </div>
      </div>

      <div className="my-10 border-b border-gray-300"></div>

      <div className="mb-10">
        {" "}
        <label className="block text-gray-700 mb-2">선호하는 나이대</label>
        <RangeSelector
          options={[
            { name: "20대", value: 1 },
            { name: "30대", value: 2 },
            { name: "40대", value: 3 },
            { name: "50대", value: 4 },
            { name: "60대 이상", value: 5 },
          ]}
          onChange={(value) => handleSelected("preferredAge", value)}
          name="preferredAge"
        />
      </div>

      {/* 구분선 추가 */}
      <div className="my-6 border-t border-gray-300"></div>

      <button
        onClick={handleNext}
        disabled={isDisabled}
        className={`w-full py-2 rounded-md text-white mt-6 ${
          isDisabled ? "bg-gray-400" : "bg-blue-600 hover:bg-blue-700"
        } transition duration-200`}
      >
        다음 페이지
      </button>
    </div>
  );
}

const surveyItemKeys = [
  { key: "food", name: "음식" },
  { key: "shopping", name: "쇼핑" },
  { key: "natureTourism", name: "자연관광" },
  { key: "culturalTourism", name: "문화관광" },
  { key: "historicalTourism", name: "역사관광" },
  { key: "leisureSports", name: "레저/스포츠" },
];

const surveyItemOptions = [
  { name: "1", value: 1 },
  { name: "2", value: 2 },
  { name: "3", value: 3 },
  { name: "4", value: 4 },
  { name: "5", value: 5 },
];

function Survey2Page({ onNext }) {
  const [selected, setSelected] = useState(
    surveyItemKeys.reduce((acc, item) => {
      acc[item.key] = 1; // 초기값을 1로 설정하여 모든 항목이 payload에 포함되도록 설정
      return acc;
    }, {})
  );

  const isDisabled = Object.values(selected).some((value) => !value);

  const handleSelected = (key, value) => {
    const updatedSelected = { ...selected, [key]: value };
    console.log("Updated selected:", updatedSelected);
    setSelected(updatedSelected);
  };

  return (
    <div className="bg-white bg-opacity-90 p-8 rounded-lg shadow-lg max-w w-full">
      <h1 className="text-3xl font-bold text-center mb-6">VOYAGE CONNECT</h1>
      <h2 className="text-xl font-bold mb-4">| 주제별 선호도</h2>

      <div className="my-6 border-b border-gray-300"></div>

      {/* 첫 번째 줄에만 12345 표시 */}
      <div className="flex items-center mb-4">
        <div className="w-1/2 text-left pr-4">
          <span className="font-semibold"></span>
        </div>
        <div className="flex items-center justify-around w-full">
          {surveyItemOptions.map((option) => (
            <span key={option.value} className="text-gray-500">
              {option.value}
            </span>
          ))}
        </div>
      </div>

      {surveyItemKeys.map((item) => (
        <div key={item.key} className="flex items-center mb-4">
          <div className="w-1/2 text-left pr-4">
            <span className="font-semibold">{item.name}</span>
          </div>
          <div className="flex items-center justify-around w-full">
            {surveyItemOptions.map((option) => (
              <div
                key={`${item.key}-${option.value}`}
                className="flex flex-col items-center space-y-1"
              >
                <div
                  className={`w-6 h-6 my-2 rounded-full transition-all duration-200 cursor-pointer ${
                    selected[item.key] === option.value
                      ? "bg-blue-600 border-2 border-blue-600"
                      : "bg-white border-2 border-gray-300"
                  }`}
                  onClick={() => handleSelected(item.key, option.value)}
                />
              </div>
            ))}
          </div>
        </div>
      ))}

      <button
        onClick={() => !isDisabled && onNext(selected)} // 비활성화가 아닐 때만 onNext 호출
        disabled={isDisabled}
        className={`w-full py-2 rounded-md text-white ${
          isDisabled ? "bg-gray-400" : "bg-blue-600 hover:bg-blue-700"
        } transition duration-200`}
      >
        설문조사 완료
      </button>
    </div>
  );
}

const RangeSelector = ({
  options,
  onChange,
  defaultValue,
  name = "",
  isNoLabel = false,
}) => {
  const [selectedValue, setSelectedValue] = useState(defaultValue);

  const handleClick = (value) => {
    setSelectedValue(value);
    onChange?.(value, name);
  };

  return (
    <div className="flex-1">
      <div className="relative">
        <div className="w-full max-w">
          {" "}
          {/* 최대 가로 크기 제한 */}
          <div className="flex justify-between space-x-20">
            {" "}
            {/* 여백 추가 */}
            {options.map((option, index) => (
              <div
                key={`${name}-${index}`}
                onClick={() => handleClick(option.value)}
                className="flex flex-col items-center cursor-pointer"
              >
                {!isNoLabel && <div>{option.name}</div>}
                <div
                  className={`w-6 h-6 my-2 rounded-full transition-all duration-200 ${
                    selectedValue === option.value
                      ? "bg-blue-600 border-2 border-blue-600"
                      : "bg-white border-2 border-gray-300"
                  }`}
                />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
