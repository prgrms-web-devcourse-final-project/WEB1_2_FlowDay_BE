package org.example.flowday.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.dto.ChatMessage;
import org.example.flowday.domain.chat.dto.ChatResponse;
import org.example.flowday.global.security.util.JwtUtil;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;
import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final JwtUtil jwtUtil;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/rooms/{roomId}")
    public ChatResponse chatting(
            @DestinationVariable Long roomId,
            ChatMessage chatMessage
    ) {
        // TODO : 인증 처리
        // @Header("Authorization") String bearerToken,
//        String accessToken = resolveToken(bearerToken);
//        Long senderId = getIdByValidating(accessToken);

        LocalDateTime time = LocalDateTime.now();
        // TODO : time을 포함한 채팅 로그 저장해야함 (동기, 비동기)

        String responseMessage = HtmlUtils.htmlEscape(chatMessage.message());
        return new ChatResponse(3L, responseMessage, time);
    }

    private Long getIdByValidating(String token) {
        if (jwtUtil.isExpired(token)) {
            throw new IllegalArgumentException("[ERROR] 이미 만료된 토큰입니다.");
        }

        Long id = jwtUtil.getId(token);
        Objects.requireNonNull(id, "[ERROR] 인증 id가 존재하지 않습니다.");
        return id;
    }

    private String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}