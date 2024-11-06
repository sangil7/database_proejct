import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getMatchMessages, sendMessage } from "../api/match";
import io from "socket.io-client";
import { motion } from "framer-motion"; // framer-motion 라이브러리 사용
import Icon from "./image/icon.png";

const socket = io("http://localhost:3000"); // WebSocket 서버 URL (예시)

const STEPS = {
  INIT: "INIT",
  LOADING: "LOADING",
  CHATTING: "CHATTING",
};

const Match = () => {
  const [step, setStep] = useState(STEPS.INIT);
  const user1 = "user1";
  const user2 = "user2";

  const startMatching = () => {
    setStep(STEPS.LOADING);
    setTimeout(() => {
      setStep(STEPS.CHATTING);
    }, 3000); // 3초 후에 채팅방으로 이동
  };

  return (
    <div className="flex items-center justify-center h-screen bg-gradient-to-r from-[#6a11cb] to-[#2575fc] relative">
      <img
        src={Icon}
        alt="설명"
        className="absolute inset-0 w-full h-full object-cover opacity-30"
      />
      <div className="bg-white p-10 rounded-lg shadow-lg text-center w-1/3 z-10">
        <h1 className="text-4xl font-bold mb-6 text-gray-800">랜덤 매칭</h1>
        <p className="text-gray-600 mb-4">새로운 친구와 대화를 나눠 보세요!</p>
        {step === STEPS.INIT && (
          <button
            className="bg-blue-600 text-white px-8 py-4 rounded-lg shadow-md hover:bg-blue-700 transition duration-300"
            onClick={startMatching}
          >
            랜덤 매칭 시작
          </button>
        )}
        {step === STEPS.LOADING && <LoadingScreen />}
        {step === STEPS.CHATTING && (
          <ChattingStep user1={user1} user2={user2} />
        )}
      </div>
    </div>
  );
};

export default Match;

// 로딩 화면 컴포넌트
const LoadingScreen = () => (
  <div className="flex flex-col items-center justify-center">
    <motion.div
      animate={{ rotate: 360 }}
      transition={{ repeat: Infinity, duration: 1 }}
      className="mb-4"
    >
      <div className="w-12 h-12 border-4 border-t-transparent border-blue-600 rounded-full animate-spin"></div>
    </motion.div>
    <p className="text-xl font-semibold text-gray-700">Loading ... 💬</p>
  </div>
);

// useDelayAction 커스텀 훅
const useDelayAction = () => {
  const delayAction = ({ action, delay: delayTime = 3000 }) => {
    const timer = setTimeout(action, delayTime);
    return () => clearTimeout(timer);
  };

  return { delayAction };
};

const COMPANY_POPUP_DELAY = 5000; // 몇초 뒤에 팝업 표시할건지

const ChattingStep = ({ user1, user2 }) => {
  const [isChatting, setIsChatting] = useState(true);
  const [popupVisible, setPopupVisible] = useState(false);

  const { delayAction } = useDelayAction();

  // 팝업 표시, 채팅 비활성화
  const onPopupVisible = () => {
    setPopupVisible(true);
    setIsChatting(false);
  };

  // 팝업 닫기, 채팅 활성화
  const onPopupClose = () => {
    setPopupVisible(false);
    setIsChatting(true);
  };

  useEffect(() => {
    delayAction({
      action: onPopupVisible,
      delay: COMPANY_POPUP_DELAY, // 5초 뒤에 팝업 표시
    });
  }, []);

  return (
    <div>
      <h2>채팅방</h2>
      <ChatRoom disableChat={!isChatting} />
      {popupVisible && <CompanyPopup onClose={onPopupClose} />}
    </div>
  );
};

// 채팅창
function ChatRoom({ disableChat }) {
  const [input, setInput] = useState("");
  const [messages, setMessages] = useState([
    { user: "me", message: "익명1님이 입장하셨습니다." },
    { user: "other", message: "익명2님이 입장하셨습니다." },
  ]);

  // 내가 보낸 메시지
  const onSendMessage = (message) => {
    if (message.trim()) {
      setMessages((prevMessages) => [
        ...prevMessages,
        {
          user: "me",
          message,
        },
      ]);
      setInput("");
    }
  };

  // 엔터키 눌렀을 때 메시지 전송
  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      onSendMessage(input);
    }
  };

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        height: "80vh",
        maxWidth: "500px",
        margin: "auto",
        border: "1px solid #e0e0e0",
        borderRadius: "15px",
        overflow: "hidden",
        boxShadow: "0px 4px 10px rgba(0, 0, 0, 0.1)",
      }}
    >
      <div
        style={{
          flex: 1,
          padding: "10px",
          overflowY: "scroll",
          display: "flex",
          flexDirection: "column",
          gap: "10px",
          backgroundColor: "#f5f5f7",
        }}
      >
        {messages.map((msg, index) => (
          <div
            key={index}
            style={{
              display: "flex",
              justifyContent: msg.user === "me" ? "flex-end" : "flex-start",
            }}
          >
            <div
              style={{
                maxWidth: "70%",
                padding: "10px",
                borderRadius: "10px",
                backgroundColor: msg.user === "me" ? "#DCF8C6" : "#ffffff",
                color: "#000",
                boxShadow: "0 1px 2px rgba(0, 0, 0, 0.1)",
                whiteSpace: "pre-wrap",
                wordWrap: "break-word",
                overflowWrap: "break-word",
              }}
            >
              {msg.message}
            </div>
          </div>
        ))}
      </div>

      <div
        style={{
          display: "flex",
          padding: "10px",
          borderTop: "1px solid #e0e0e0",
          backgroundColor: "#ffffff",
        }}
      >
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyDown} // 엔터키 이벤트 추가
          disabled={disableChat}
          placeholder="메시지를 입력하세요"
          style={{
            flex: 1,
            height: "45px", // 입력창 높이 설정
            padding: "0 15px", // 상하 패딩을 줄여서 높이 맞춤
            borderRadius: "20px",
            border: "1px solid #e0e0e0",
            outline: "none",
            fontSize: "16px",
            marginTop: "20px",
          }}
        />
        <button
          onClick={() => onSendMessage(input)}
          disabled={disableChat}
          style={{
            marginLeft: "10px",
            height: "45px", // 버튼 높이 설정
            padding: "0 15px", // 상하 패딩을 줄여서 높이 맞춤
            borderRadius: "20px", // 버튼 모서리 둥글게
            backgroundColor: "#34b7f1",
            color: "#fff",
            border: "none",
            fontSize: "14px", // 버튼 폰트 크기
            cursor: "pointer",
            outline: "none",
            width: "60px", // 버튼 너비 설정
            marginTop: "20px",
          }}
        >
          전송
        </button>
      </div>
    </div>
  );
}

// 일정 시간 뒤에 나타나는 동행하기 팝업창
function CompanyPopup({ onClose }) {
  const navigate = useNavigate();

  const handleContinue = () => {
    onClose();
  };

  const handleQuit = () => {
    navigate("/home");
  };

  return (
    <div
      style={{
        position: "fixed",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        width: "100%",
        height: "100%",
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: 100,
      }}
    >
      <div
        style={{
          backgroundColor: "white",
          padding: "30px", // 패딩 증가
          borderRadius: "15px", // 모서리 둥글기 증가
          display: "flex",
          flexDirection: "column",
          gap: "20px", // 내부 요소 간격 증가
          minWidth: "400px", // 너비 확대
          maxWidth: "500px", // 최대 너비 설정
        }}
      >
        <h3
          style={{
            fontSize: "1.5rem",
            fontWeight: "bold",
            textAlign: "center",
          }}
        >
          동행을 하시겠습니까?
        </h3>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            gap: "15px",
          }}
        >
          {" "}
          {/* 버튼 사이 간격 추가 */}
          <button
            style={{ flex: 1, padding: "10px", fontSize: "1rem" }}
            onClick={handleContinue}
          >
            동행하기
          </button>
          <button
            style={{ flex: 1, padding: "10px", fontSize: "1rem" }}
            onClick={handleQuit}
          >
            그만하기
          </button>
        </div>
      </div>
    </div>
  );
}
