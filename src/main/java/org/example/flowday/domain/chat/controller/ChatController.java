package org.example.flowday.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.dto.ChatMessage;
import org.example.flowday.domain.chat.dto.ChatResponse;
import org.example.flowday.domain.chat.service.ChatService;
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
    private final ChatService chatService;

    /**
     * 웹 소켓 연결
     */
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/rooms/{roomId}")
    public ChatResponse chatting(
            @DestinationVariable Long roomId,
            ChatMessage chatMessage
    ) {
        // TODO : 인증
//        @Header("Authorization") String bearerToken,
//        String accessToken = resolveToken(bearerToken);
//        Long senderId = getIdByValidating(accessToken);

        LocalDateTime time = LocalDateTime.now();
        String responseMessage = HtmlUtils.htmlEscape(chatMessage.message());

        // TODO : 채팅 로그 저장 (동기 -> 비동기), senderId 넣어야 함
        chatService.saveMessage(roomId, 99L, responseMessage, time);

        return new ChatResponse(99L, responseMessage, time);
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