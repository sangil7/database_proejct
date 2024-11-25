package com.tripmate.chat.repository;

import com.tripmate.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 특정 사용자 ID가 포함된 채팅방을 찾는 메서드
    List<ChatRoom> findByParticipantsId(Long userId);
}
