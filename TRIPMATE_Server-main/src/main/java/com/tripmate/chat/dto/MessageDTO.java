package com.tripmate.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {

    private Long chatRoomId; // 채팅방 id
    private Long senderId; // 메시지 보낸 사람의 ID
    private String content; // 메시지 내용

}
