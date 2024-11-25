package com.tripmate.chat.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.tripmate.chat.dto.MessageDTO;
import com.tripmate.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;

    /**
     * Socket.IO 연결 이벤트
     */
    @OnEvent("connect")
    public void onConnect(SocketIOClient client) {
        client.sendEvent("connection", "Connected to Socket.IO server");
    }

    /**
     * 메시지 전송 처리
     */
    @OnEvent("sendMessage")
    public void handleMessage(SocketIOClient client, MessageDTO messageDTO) {
        // 메시지 저장
        messageService.sendMessage(messageDTO);

        // 동일 채팅방의 모든 사용자에게 메시지 전송
        client.getNamespace().getRoomOperations(messageDTO.getChatRoomId().toString())
                .sendEvent("message", messageDTO);
    }

    /**
     * 특정 채팅방 참여
     */
    @OnEvent("joinRoom")
    public void joinRoom(SocketIOClient client, String roomId) {
        client.joinRoom(roomId);
        client.sendEvent("joinedRoom", "Joined room: " + roomId);
    }
}
