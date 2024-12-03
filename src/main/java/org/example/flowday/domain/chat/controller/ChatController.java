package org.example.flowday.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.dto.ChatMessage;
import org.example.flowday.domain.chat.dto.ChatResponse;
import org.example.flowday.domain.chat.service.ChatService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;

    /**
     * 웹 소켓 연결
     */
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/rooms/{roomId}")
    public ChatResponse chatting(
            @DestinationVariable Long roomId,
            @AuthenticationPrincipal SecurityUser user,
            ChatMessage chatMessage
    ) {
        Long senderId = user.getId();
        LocalDateTime time = LocalDateTime.now();
        String responseMessage = HtmlUtils.htmlEscape(chatMessage.message());

        // TODO : 채팅 로그 저장 (동기 -> 비동기)
        chatService.saveMessage(roomId, senderId, responseMessage, time);
        // 페이지 정보는 웹소켓 연결에서는 의미 없으므로 0으로 설정
        return new ChatResponse(senderId, responseMessage, time, 0, 0);
    }
}