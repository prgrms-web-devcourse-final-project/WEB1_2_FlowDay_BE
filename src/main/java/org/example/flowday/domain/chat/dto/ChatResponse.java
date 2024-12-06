package org.example.flowday.domain.chat.dto;

import org.example.flowday.domain.chat.entity.ChatMessageDocument;
import java.time.LocalDateTime;

public record ChatResponse(
        Long senderId,
        String message,
        LocalDateTime time,
        Integer pageNumber,  // 현재 페이지 번호
        Integer totalPages   // 총 페이지 수
) {
    public static ChatResponse from(
            final ChatMessageDocument chatMessageDocument,
            Integer pageNumber,
            Integer totalPages
    ) {
        return new ChatResponse(
                chatMessageDocument.getFromId(),
                chatMessageDocument.getTextMessage(),
                chatMessageDocument.getSendTime(),
                pageNumber,
                totalPages
        );
    }
}