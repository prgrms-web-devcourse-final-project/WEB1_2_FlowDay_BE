package org.example.flowday.domain.chat.repository;

import org.bson.types.ObjectId;
import org.example.flowday.domain.chat.entity.ChatMessageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageDocumentRepository extends MongoRepository<ChatMessageDocument, ObjectId> {
    Page<ChatMessageDocument> findByChatRoomId(Long chatRoomId, Pageable pageable);
}
