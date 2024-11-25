package com.tripmate.chat.service;

import com.tripmate.chat.domain.ChatRoom;
import com.tripmate.chat.repository.ChatRoomRepository;
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

class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createChatRoom_ShouldCreateAndReturnChatRoom() {
        // given
        User user1 = new User();
        user1.setUserId("user1");

        User user2 = new User();
        user2.setUserId("user2");

        when(userRepository.findByUserId("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findByUserId("user2")).thenReturn(Optional.of(user2));

        ChatRoom chatRoom = new ChatRoom();
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        // when
        ChatRoom result = chatRoomService.createChatRoom("user1", "user2");

        // then
        assertNotNull(result);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    void createChatRoom_ShouldThrowException_WhenUserNotFound() {
        // given
        when(userRepository.findByUserId("user1")).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            chatRoomService.createChatRoom("user1", "user2");
        });

        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }
}
