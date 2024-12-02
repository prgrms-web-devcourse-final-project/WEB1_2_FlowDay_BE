package org.example.flowday.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_no", nullable = false)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column
    private Long fromId;

    @Column(nullable = false)
    private String textMessage;

    @Column(nullable = false)
    private LocalDateTime sendTime;

    @Builder
    public ChatMessageEntity(
            final Long id,
            final Long chatRoomId,
            final Long fromId,
            final String textMessage,
            final LocalDateTime sendTime
    ) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.fromId = fromId;
        this.textMessage = textMessage;
        this.sendTime = sendTime;
    }

    public static ChatMessageEntity create(
            final Long chatRoomId,
            final Long fromId,
            final String textMessage,
            final LocalDateTime sendTime
    ) {
        return ChatMessageEntity.builder()
                .chatRoomId(chatRoomId)
                .fromId(fromId)
                .textMessage(textMessage)
                .sendTime(sendTime)
                .build();
    }
}
