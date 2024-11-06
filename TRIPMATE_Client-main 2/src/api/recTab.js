import axios from "axios";

// 위치 정보를 보내고 추천 장소 정보를 가져오는 함수
export const getRecommendedLocations = async (userId, latitude, longitude) => {
  try {
    // localhost:5000을 base URL로 사용하는 axios 인스턴스 생성
    const axiosLocalInstance = axios.create({
      baseURL: "http://54.180.134.119:5000",
    });

    const response = await axiosLocalInstance.post("/recommend", {
      user_id: userId,
      latitude: latitude,
      longitude: longitude,
    });

    if (response.data && response.data.results) {
      return response.data.results.map((location) => ({
        name: location.name || "Unknown",
        mapId: location.mapId || "N/A",
        description: location.description || "No description available",
        latitude: location.latitude,
        longitude: location.longitude,
      }));
    } else {
      throw new Error("Unexpected response format from server");
    }
  } catch (error) {
    console.error("Error fetching recommended locations:", error);
    throw new Error("Failed to fetch recommended locations.");
  }
};
