package org.example.flowday.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.entity.ChatMessageDocument;
import org.example.flowday.domain.chat.entity.ChatRoomEntity;
import org.example.flowday.domain.chat.event.dto.ChatMessageEvent;
import org.example.flowday.domain.chat.repository.ChatMessageDocumentRepository;
import org.example.flowday.domain.chat.repository.ChatRoomRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatService {
    private final ChatMessageDocumentRepository chatMessageDocumentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 채팅 방 생성
     */
    @Transactional
    public Long registerChatRoom(final LocalDateTime time) {
        ChatRoomEntity chatRoom = ChatRoomEntity.create(time);
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);
        Long chatRoomId = savedChatRoom.getId();
        return chatRoomId;
    }

    /**
     * [비동기 - 스프링 이벤트] 채팅 메세지 저장
     */
    public void saveMessage(
            final Long roomId,
            final Long senderId,
            final String responseMessage,
            final LocalDateTime time
    ) {
        applicationEventPublisher.publishEvent(
                new ChatMessageEvent(roomId, senderId, responseMessage, time)
        );
    }

    /**
     * [NoSQL] 채팅 메세지 조회
     */
    public Page<ChatMessageDocument> getPagedChatMessages(
            final Long roomId,
            final Pageable pageable
    ) {
        return chatMessageDocumentRepository.findByChatRoomId(roomId, pageable);
    }

    /**
     * 채팅 방 삭제
     */
    @Transactional
    public Long deleteChatRoom(final Long roomId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));

        chatRoomRepository.delete(chatRoom);
        return roomId;
    }
}
