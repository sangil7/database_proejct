package com.tripmate.chat.repository;

import com.tripmate.chat.domain.Message;
import com.tripmate.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지를 시간순으로 가져오는 메서드
    List<Message> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);
}
