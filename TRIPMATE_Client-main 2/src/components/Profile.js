import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Image from "./image/image1.jpg";

function Profile() {
  const navigate = useNavigate();
  const [token, setToken] = useState(localStorage.getItem("token"));

  const [profile, setProfile] = useState({
    age: "",
    gender: "",
    profileImage: "",
    mbti: "",
    introduction: "",
    locationCountry: null,
    locationRegion: null,
  });

  const isDisabled =
    !profile.age || !profile.mbti || !profile.gender || !profile.introduction;

  // 토큰 업데이트 감지
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

  // useCallback으로 fetchProfile 함수 정의
  const fetchProfile = useCallback(async () => {
    try {
      console.log("Access Token:", token);
      if (!token) {
        throw new Error("No access token found. Make sure you are logged in.");
      }

      const response = await axios.get(
        "http://localhost:8080/api/users/me/profile",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setProfile(response.data);
    } catch (error) {
      console.error("Error fetching profile:", error);
    }
  }, [token]); // token이 변경될 때마다 fetchProfile 재정의

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]); // fetchProfile을 의존성 배열에 추가

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!token) {
        throw new Error("No access token found. Make sure you are logged in.");
      }

      // profileImage를 일반 string으로 설정 (예: 테스트용 이미지 URL 사용)
      const profileData = {
        ...profile,
        profileImage: profile.profileImage || "https://example.com/image.jpg", // 테스트용 이미지 URL
      };

      const response = await axios.post(
        "http://localhost:8080/api/users/me/profile",
        profileData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      console.log("Profile saved successfully:", response.data);
      alert("프로필 저장 완료");

      if (response.data.nextStep === "survey") {
        navigate("/survey");
      }
    } catch (error) {
      console.error("Error saving profile:", error);
      alert("프로필 저장 중 오류 발생");
    }
  };

  const onChangeProfile = (key, value) => {
    setProfile({ ...profile, [key]: value });
  };

  return (
    <body class="flex items-center justify-center min-h-screen bg-gray-100">
      <div class="bg-white p-8 rounded-lg shadow-lg max-w-md w-full z-10">
        <h1 class="text-center text-2xl font-bold mb-6">VOYAGE CONNECT</h1>
        <h2 class="text-lg font-semibold mb-4">| 프로필</h2>
        <form onSubmit={handleSubmit}>
          <div class="mb-4">
            <label class="block text-sm font-medium mb-1" for="age">
              나이
            </label>
            <input
              type="number"
              value={profile.age}
              onChange={(e) => onChangeProfile("age", e.target.value)}
              placeholder="Age"
            />
          </div>
          <div class="mb-4">
            <div class="flex items-center space-x-4">
              <div class="flex-grow">
                <label class="block text-sm font-medium mb-1" for="mbti">
                  MBTI
                </label>
                <select
                  value={profile.mbti}
                  onChange={(e) => onChangeProfile("mbti", e.target.value)}
                >
                  <option>선택하세요</option>
                  <option value="INTJ">INTJ</option>
                  <option value="INTP">INTP</option>
                  <option value="INFJ">INFJ</option>
                  <option value="INFP">INFP</option>
                  <option value="ISTJ">ISTJ</option>
                  <option value="ISTP">ISTP</option>
                  <option value="ISFJ">ISFJ</option>
                  <option value="ISFP">ISFP</option>
                  <option value="ENTJ">ENTJ</option>
                  <option value="ENTP">ENTP</option>
                  <option value="ENFJ">ENFJ</option>
                  <option value="ENFP">ENFP</option>
                  <option value="ESTJ">ESTJ</option>
                  <option value="ESTP">ESTP</option>
                  <option value="ESFJ">ESFJ</option>
                  <option value="ESFP">ESFP</option>
                </select>
              </div>

              <div class="flex-grow">
                <label class="block text-sm font-medium mb-1" for="gender">
                  성별
                </label>
                <select
                  value={profile.gender}
                  onChange={(e) => onChangeProfile("gender", e.target.value)}
                >
                  <option>선택하세요</option>
                  <option>남성</option>
                  <option>여성</option>
                </select>
              </div>
            </div>
          </div>

          <div class="mb-4">
            <label class="block text-sm font-medium mb-1" for="bio">
              자기소개
            </label>
            <input
              type="text"
              value={profile.introduction}
              onChange={(e) => onChangeProfile("introduction", e.target.value)}
              placeholder="Self Introduction"
            />
          </div>
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center">
              <div class="w-16 h-16 bg-gray-200 rounded-full flex items-center justify-center">
                <svg
                  class="w-16 h-8 text-gray-500"
                  fill="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
                </svg>
              </div>
            </div>
            <input
              type="text"
              value={profile.profileImage || ""}
              onChange={(e) => onChangeProfile("profileImage", e.target.value)}
              placeholder="Enter profile image URL"
              style={{ width: "300px" }}
            />
          </div>
          <button
            type="submit"
            class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
          >
            등록하기
          </button>
        </form>
      </div>
      <div>
        <img
          src={Image}
          alt="설명"
          className="absolute inset-0 w-full h-full object-cover opacity-50 z-0"
        />
      </div>
    </body>
  );
}

export default Profile;
