package com.tripmate.chat.domain;

import com.tripmate.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)  // Lazy Loading 적용
    @JoinTable(
            name = "user_chatroom",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "userId") // user_id -> userId
    )
    private Set<User> participants = new HashSet<>(); // NullPointerException 방지

    @OneToMany(mappedBy = "chatRoom", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 채팅방 생성 시간

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 마지막 메시지 시간

    // 생성 시 자동으로 시간 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 업데이트 시 자동으로 시간 갱신
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 메시지 추가 시 updatedAt을 갱신하는 메서드 추가
    public void addMessage(Message message) {
        messages.add(message);
        message.setChatRoom(this);
        this.updatedAt = LocalDateTime.now(); // 메시지 추가 시에도 updatedAt 갱신
    }

    // 참여자 추가 시 updatedAt을 갱신하는 메서드 추가
    public void addParticipant(User user) {
        participants.add(user);
        this.updatedAt = LocalDateTime.now(); // 참여자 추가 시에도 updatedAt 갱신
    }
}
