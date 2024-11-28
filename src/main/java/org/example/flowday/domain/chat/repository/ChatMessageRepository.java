package org.example.flowday.domain.chat.repository;

import org.example.flowday.domain.chat.entity.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    Page<ChatMessageEntity> findByChatRoomId(Long roomId, Pageable pageable);

}
