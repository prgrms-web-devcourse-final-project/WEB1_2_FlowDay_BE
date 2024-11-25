package org.example.flowday.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatResponse(
        Long senderId,
        String message,
        LocalDateTime time
) {
}