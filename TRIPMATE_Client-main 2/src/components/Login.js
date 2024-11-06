import React, { useEffect } from "react";
import Image from "./image/image2.jpg";

const LoginPage = () => {
  useEffect(() => {
    // Kakao SDK 초기화
    if (!window.Kakao.isInitialized()) {
      window.Kakao.init("be95001b9262be92ca9f7558d83cf1f0");
    }
  }, []);

  const handleKakaoLogin = () => {
    // 로그인 버튼 클릭 시 Kakao 인증 페이지로 이동
    window.Kakao.Auth.authorize({
      redirectUri: "http://localhost:3000/callback", // 프론트엔드의 Redirect URI
    });
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100 relative">
      <img
        src={Image}
        alt="설명"
        className="absolute inset-0 w-full h-full object-cover opacity-90 z-0"
      />
      <div className="bg-white bg-opacity-90 p-10 rounded-lg shadow-md max-w-sm w-full z-10 relative">
        <h1 className="text-4xl font-bold text-center mb-4 text-blue-700">
          VOYAGE ✈️ CONNECT
        </h1>
        <p className="text-gray-600 text-center mb-6">
          새로운 만남과 여행 경험을 쌓아보세요!
        </p>
        <form className="space-y-4">
          <button
            onClick={handleKakaoLogin}
            type="button"
            className="w-full py-3 bg-yellow-400 text-black font-semibold rounded-md shadow-md hover:bg-yellow-500 flex items-center justify-center transition duration-200 ease-in-out"
          >
            <img
              src="https://img.icons8.com/color/48/000000/kakaotalk.png"
              alt="카카오톡 아이콘"
              className="h-5 w-5 mr-2"
            />
            카카오 로그인
          </button>
        </form>
        <div className="mt-4 text-center"></div>
      </div>
    </div>
  );
};

export default LoginPage;
