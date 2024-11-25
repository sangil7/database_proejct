package com.tripmate.chat.service;

import com.tripmate.chat.domain.ChatRoom;
import com.tripmate.chat.domain.Message;
import com.tripmate.chat.dto.MessageDTO;
import com.tripmate.chat.repository.ChatRoomRepository;
import com.tripmate.chat.repository.MessageRepository;
import com.tripmate.user.domain.User;
import com.tripmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendMessage_ShouldSaveAndReturnMessage() {
        // given
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);

        User sender = new User();
        sender.setId(1L);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setChatRoomId(1L);
        messageDTO.setSenderId(1L);
        messageDTO.setContent("Hello!");

        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        Message message = new Message();
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // when
        Message result = messageService.sendMessage(messageDTO);

        // then
        assertNotNull(result);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void sendMessage_ShouldThrowException_WhenChatRoomNotFound() {
        // given
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.empty());

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setChatRoomId(1L);
        messageDTO.setSenderId(1L);
        messageDTO.setContent("Hello!");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.sendMessage(messageDTO);
        });

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendMessage_ShouldThrowException_WhenSenderNotFound() {
        // given
        ChatRoom chatRoom = new ChatRoom();
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setChatRoomId(1L);
        messageDTO.setSenderId(1L);
        messageDTO.setContent("Hello!");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.sendMessage(messageDTO);
        });

        verify(messageRepository, never()).save(any(Message.class));
    }
}
