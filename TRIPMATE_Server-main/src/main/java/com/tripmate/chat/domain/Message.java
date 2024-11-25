package com.tripmate.chat.domain;

import com.tripmate.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom; // 메시지가 속한 채팅방

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 메시지를 보낸 사용자

    @Column(nullable = false)
    private String content; // 메시지 내용

    @Column(nullable = false)
    private String messageType; // 메시지 타입 (예: 텍스트, 이미지, 파일)

    @Column(nullable = false)
    private LocalDateTime sentAt; // 메세지 전송 시간
}
