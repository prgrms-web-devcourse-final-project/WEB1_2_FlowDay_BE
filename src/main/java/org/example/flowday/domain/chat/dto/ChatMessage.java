package org.example.flowday.domain.chat.dto;

public record ChatMessage(
        String message,
        String senderId
) {
}