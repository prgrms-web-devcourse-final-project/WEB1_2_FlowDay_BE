package org.example.flowday.domain.chat.dto;

import org.example.flowday.domain.chat.entity.ChatMessageEntity;
import java.time.LocalDateTime;

public record ChatResponse(
        Long senderId,
        String message,
        LocalDateTime time
) {
    public static ChatResponse from(final ChatMessageEntity chatMessageEntity) {
        return new ChatResponse(
                chatMessageEntity.getFromId(),
                chatMessageEntity.getTextMessage(),
                chatMessageEntity.getSendTime()
        );
    }
}