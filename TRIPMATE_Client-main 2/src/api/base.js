import axios from "axios";

// .env 파일에서 API URL 가져오기
const BASE_URL = process.env.REACT_APP_API_URL; // 환경 변수에서 URL 가져오기

const axiosInstance = axios.create({
  baseURL: BASE_URL, // 환경 변수로 변경
});

console.log("Base URL:", process.env.REACT_APP_API_URL); // baseURL 값 확인

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token"); // JWT 토큰 가져오기
    console.log("Token:", token); // 토큰이 null이 아닌지 확인
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;
