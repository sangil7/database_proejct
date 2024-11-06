import React from "react";
import "./Modal.css"; // CSS 파일 임포트

const Modal = ({ isOpen, onClose, name }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>환영합니다, 여행자님!</h2>
        <button onClick={onClose} className="close-button">
          닫기
        </button>
        <div className="fireworks">
          {/* 폭죽 애니메이션을 위한 div들 */}
          <div className="firework"></div>
          <div className="firework"></div>
          <div className="firework"></div>
        </div>
      </div>
    </div>
  );
};

export default Modal;
