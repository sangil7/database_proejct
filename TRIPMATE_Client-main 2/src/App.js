import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import Profile from "./components/Profile";
import Home from "./components/Home";
import CallbackPage from "./components/CallbackPage";
import Survey from "./components/Survey";
import RecTab from "./components/RecTab";
import Match from "./components/Match";
import Sns from "./components/Sns";
import "./styles.css";

console.log(process.env.REACT_APP_API_URL); // 올바르게 로딩되었는지 확인

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/home" element={<Home />} />
      <Route path="/survey" element={<Survey />} />
      <Route path="/rectab" element={<RecTab />} />
      <Route path="/callback" element={<CallbackPage />} />
      <Route path="/match" element={<Match />} />
      <Route path="/sns" element={<Sns />} />
    </Routes>
  );
}

export default App;
