package org.example.flowday.domain.chat.event;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.entity.ChatMessageDocument;
import org.example.flowday.domain.chat.event.dto.ChatMessageEvent;
import org.example.flowday.domain.chat.repository.ChatMessageDocumentRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatMessageEventHandler {
    private final ChatMessageDocumentRepository chatMessageDocumentRepository;

    @Async
    @TransactionalEventListener
    public void handle(ChatMessageEvent event) {
        Long roomId = event.roomId();
        Long senderId = event.senderId();
        String responseMessage = event.responseMessage();
        LocalDateTime time = event.time();

        ChatMessageDocument chatMessageDocument = ChatMessageDocument.create(
                roomId,
                senderId,
                responseMessage,
                time
        );
        chatMessageDocumentRepository.save(chatMessageDocument);
    }
}
