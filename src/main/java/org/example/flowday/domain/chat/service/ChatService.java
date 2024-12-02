package org.example.flowday.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.entity.ChatMessageEntity;
import org.example.flowday.domain.chat.entity.ChatRoomEntity;
import org.example.flowday.domain.chat.repository.ChatMessageRepository;
import org.example.flowday.domain.chat.repository.ChatRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅 방 생성
     */
    public Long registerChatRoom(final LocalDateTime time) {
        ChatRoomEntity chatRoom = ChatRoomEntity.create(time);
        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);
        Long chatRoomId = savedChatRoom.getId();
        return chatRoomId;
    }

    /**
     * 채팅 메세지 저장
     */
    public void saveMessage(
            final Long roomId,
            final Long senderId,
            final String responseMessage,
            final LocalDateTime time
    ) {
        ChatMessageEntity chatMessage = ChatMessageEntity.create(
                roomId,
                senderId,
                responseMessage,
                time
        );
        chatMessageRepository.save(chatMessage);
    }

    /**
     * 채팅 조회
     */
    public Page<ChatMessageEntity> getPagedChatMessages(
            final Long roomId,
            final Pageable pageable
    ) {
        return chatMessageRepository.findByChatRoomId(roomId, pageable);
    }

    /**
     * 채팅 방 삭제
     */
    public Long deleteChatRoom(final Long roomId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));

        chatRoomRepository.delete(chatRoom);
        return roomId;
    }
}
