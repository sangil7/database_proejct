package com.tripmate.chat.service;

import com.tripmate.chat.domain.ChatRoom;
import com.tripmate.chat.domain.Message;
import com.tripmate.chat.dto.MessageDTO;
import com.tripmate.chat.repository.MessageRepository;
import com.tripmate.chat.repository.ChatRoomRepository;
import com.tripmate.user.domain.User;
import com.tripmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 메시지 저장 및 전송 메서드
    public Message sendMessage(MessageDTO messageDTO) {

        // 채팅방과 보낸 사람 정보 조회
        ChatRoom chatRoom = chatRoomRepository.findById(messageDTO.getChatRoomId()).orElseThrow(()
                -> new IllegalArgumentException("Invalid chat room ID"));
        User sender = userRepository.findById(messageDTO.getSenderId()).orElseThrow(()
                -> new IllegalArgumentException("Invalid sender ID"));

        // 메시지 생성 및 설정
        Message message = setMessage(messageDTO, chatRoom, sender);

        // 메시지 저장
        return messageRepository.save(message);
    }

    private static Message setMessage(MessageDTO messageDTO, ChatRoom chatRoom, User sender) {
        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(messageDTO.getContent());
        message.setMessageType("TEXT");  // 텍스트 메시지로 설정 (다른 타입도 가능)
        message.setSentAt(LocalDateTime.now()); // 시간 설정
        return message;
    }

    // 특정 채팅방 메세지 전부 가져오기
    public List<Message> getMessagesByChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chat room ID"));
        return messageRepository.findByChatRoomOrderBySentAtAsc(chatRoom);
    }
}
