import axiosInstance from "./base";
import io from "socket.io-client";

const socket = io("http://localhost:3000"); // WebSocket 서버 URL (예시)

// 매칭 메시지 가져오기 함수
export const getMatchMessages = async () => {
  try {
    const response = await axiosInstance.get("/api/match/messages");
    return response.data;
  } catch (error) {
    console.error("Error fetching match messages:", error);
    throw new Error("Failed to fetch match messages.");
  }
};

// 메시지 전송 함수
export const sendMessage = (message) => {
  try {
    // WebSocket을 통해 메시지 전송
    socket.emit("sendMessage", message);
  } catch (error) {
    console.error("Error sending message:", error);
    throw new Error("Failed to send message.");
  }
};
