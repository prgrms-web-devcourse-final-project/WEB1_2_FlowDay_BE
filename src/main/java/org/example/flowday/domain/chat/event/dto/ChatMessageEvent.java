package org.example.flowday.domain.chat.event.dto;

import java.time.LocalDateTime;

public record ChatMessageEvent(
        Long roomId,
        Long senderId,
        String responseMessage,
        LocalDateTime time
) {
}
