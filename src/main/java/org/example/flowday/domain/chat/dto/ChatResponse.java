package org.example.flowday.domain.chat.dto;

import org.example.flowday.domain.chat.entity.ChatMessageEntity;
import java.time.LocalDateTime;

public record ChatResponse(
        Long senderId,
        String message,
        LocalDateTime time,
        Integer pageNumber,  // 현재 페이지 번호
        Integer totalPages   // 총 페이지 수
) {
    public static ChatResponse from(
            final ChatMessageEntity chatMessageEntity,
            Integer pageNumber,
            Integer totalPages
    ) {
        return new ChatResponse(
                chatMessageEntity.getFromId(),
                chatMessageEntity.getTextMessage(),
                chatMessageEntity.getSendTime(),
                pageNumber,
                totalPages
        );
    }
}