import axiosInstance from "./base";

// 프로필 정보 가져오기 함수
export const getProfile = async () => {
  try {
    const token = localStorage.getItem("jwtToken"); // JWT 토큰 가져오기
    const response = await axiosInstance.get("/api/users/me/profile", {
      headers: {
        Authorization: `Bearer ${token}`, // Authorization 헤더에 토큰 포함
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching profile:", error);
    throw error;
  }
};

// 프로필 등록 함수
export const registerProfile = async (profile) => {
  try {
    const token = localStorage.getItem("jwtToken"); // JWT 토큰 가져오기
    const requestData = {
      ...profile,
      locationCountry: null,
      locationRegion: null,
    };
    const response = await axiosInstance.post(
      "/api/users/me/profile",
      requestData,
      {
        headers: {
          Authorization: `Bearer ${token}`, // Authorization 헤더에 토큰 포함
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error registering profile:", error);
    throw error;
  }
};
