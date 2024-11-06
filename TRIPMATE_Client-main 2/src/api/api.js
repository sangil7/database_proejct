import axiosInstance from "./base";

// URL에서 인가 코드를 추출하는 함수
export const getKakaoCodeFromUrl = () => {
  const params = new URLSearchParams(window.location.search);
  return params.get("code");
};

// 카카오 로그인 URL을 가져오는 함수
export const getKakaoLoginUrl = async () => {
  try {
    const response = await axiosInstance.get("/api/login");
    return response.data.url;
  } catch (error) {
    console.error("Error fetching Kakao login URL:", error);
    throw new Error("Failed to fetch Kakao login URL");
  }
};

// 사용자 프로필 가져오는 함수
export const getUserProfile = async () => {
  const code = getKakaoCodeFromUrl();
  if (!code) {
    throw new Error("No authorization code found in URL");
  }

  try {
    const response = await axiosInstance.get(
      `/api/auth/kakao/callback?code=${code}`
    );
    return response.data;
  } catch (error) {
    console.error("Error fetching user profile:", error);
    throw new Error("Failed to fetch user profile");
  }
};
