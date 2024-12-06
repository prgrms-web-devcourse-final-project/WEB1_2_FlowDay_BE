package org.example.flowday.domain.chat.event;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.entity.ChatMessageEntity;
import org.example.flowday.domain.chat.event.dto.ChatMessageEvent;
import org.example.flowday.domain.chat.repository.ChatMessageRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Service
public class ChatMessageEventHandler {
    private final ChatMessageRepository chatMessageRepository;

    @Async
    @TransactionalEventListener
    public void handle(ChatMessageEvent event) {
        ChatMessageEntity chatMessage = ChatMessageEntity.create(
                event.roomId(),
                event.senderId(),
                event.responseMessage(),
                event.time()
        );
        chatMessageRepository.save(chatMessage);
    }
}
