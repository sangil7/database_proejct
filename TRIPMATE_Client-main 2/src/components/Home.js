import React from "react";
import image1 from "./image/image1.jpg";
import { useNavigate } from "react-router-dom";
import { FaPlaneDeparture, FaMapMarkedAlt, FaSignOutAlt } from "react-icons/fa";

const Home = () => {
  const navigate = useNavigate();

  const handleShowRecommendations = () => {
    navigate("/rectab");
  };

  const handleMatchButtonClick = () => {
    // 매칭 페이지로 이동하는 코드 (예: /match 경로로 이동)
    navigate("/match");
  };

  const handleLogout = () => {
    // 로그 아웃 버튼 클릭 시 동작
    alert("로그아웃 되었습니다.");
    navigate("/");
  };

  return (
    <div className=" flex items-center justify-center min-h-screen">
      {/* 로그 아웃 버튼 */}
      <div className="absolute bottom-4 right-4 flex items-center z-10">
        <button
          className="bg-white shadow-lg text-gray-800 rounded-lg p-3 hover:bg-gray-300 transition duration-300 flex items-center"
          onClick={handleLogout}
        >
          <FaSignOutAlt className="mr-2" /> {/* 아이콘 추가 */}
          Log out
        </button>
      </div>
      <img
        src={image1}
        className="absolute inset-0 w-full h-full object-cover opacity-80"
      />
      <div className="bg-white shadow-lg rounded-lg p-10 max-w-md text-center relative overflow-hidden">
        <h1 className="text-5xl font-bold text-gray-800 mb-6 relative z-10">
          Voyage Connect
        </h1>
        <p className="text-gray-600 mb-8 relative z-10">
          새로운 만남과 함께 여행 경험을 쌓아보세요!
        </p>
        <div className="flex justify-center space-x-4 relative z-10">
          <button
            className="bg-blue-600 text-white font-semibold py-4 px-6 rounded-lg shadow-lg hover:bg-blue-700 transition duration-300 flex items-center"
            onClick={handleMatchButtonClick}
          >
            <FaPlaneDeparture className="mr-2" /> 매칭하기
          </button>
          <button
            className="bg-green-600 text-white font-semibold py-4 px-6 rounded-lg shadow-lg hover:bg-green-700 transition duration-300 flex items-center"
            onClick={handleShowRecommendations}
          >
            <FaMapMarkedAlt className="mr-2" /> 추천 장소
          </button>
        </div>
      </div>
    </div>
  );
};

export default Home;
