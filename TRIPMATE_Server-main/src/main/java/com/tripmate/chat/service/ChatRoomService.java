package com.tripmate.chat.service;

import com.tripmate.chat.domain.ChatRoom;
import com.tripmate.chat.repository.ChatRoomRepository;
import com.tripmate.user.domain.User;
import com.tripmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatRoom createChatRoom(String userId1, String userId2) {

        // user DB에서 사용자 정보 가져오기
        User user1 = userRepository.findByUserId(userId1).orElse(null);
        User user2 = userRepository.findByUserId(userId2).orElse(null);

        // 채팅방 생성
        ChatRoom chatRoom = new ChatRoom();

        // HashSet을 List로 변환
        chatRoom.getParticipants().add(user1);
        chatRoom.getParticipants().add(user2);

        // 채팅방 저장
        return chatRoomRepository.save(chatRoom);

        /**
         * 채팅방 생성하고 나서 바로 채팅방으로 가게끔
         * response.put("생성완료")
         * return ResponseEntity.ok(response)
         */
    }
}
