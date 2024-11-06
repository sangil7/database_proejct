import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";

console.log(process.env.REACT_APP_API_URL); // 올바르게 로딩되었는지 확인

// Kakao SDK 초기화
window.Kakao.init("be95001b9262be92ca9f7558d83cf1f0");

const root = ReactDOM.createRoot(document.getElementById("root"));

root.render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
