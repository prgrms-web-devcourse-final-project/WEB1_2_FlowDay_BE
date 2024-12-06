package org.example.flowday.domain.chat.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(collection = ChatMessageDocument.COLLECTION_NAME)
@CompoundIndexes({
        @CompoundIndex(name = "chatRoomId_sendTime_idx", def = "{'chatRoomId': 1, 'sendTime': -1}")
})
public class ChatMessageDocument {
    public static final String COLLECTION_NAME = "chat_messages";

    @Id
    private ObjectId id;
    private Long chatRoomId;
    private Long fromId;
    private String textMessage;
    private LocalDateTime sendTime;

    @Builder
    public ChatMessageDocument(
            final ObjectId id,
            final Long chatRoomId,
            final Long fromId,
            final String textMessage,
            final LocalDateTime sendTime
    ) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.fromId = fromId;
        this.textMessage = textMessage;
        this.sendTime = sendTime;
    }

    public static ChatMessageDocument create(
            final Long chatRoomId,
            final Long fromId,
            final String textMessage,
            final LocalDateTime sendTime
    ) {
        return ChatMessageDocument.builder()
                .chatRoomId(chatRoomId)
                .fromId(fromId)
                .textMessage(textMessage)
                .sendTime(sendTime)
                .build();
    }
}
