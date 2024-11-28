package org.example.flowday.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_no", nullable = false)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    protected LocalDateTime createdAt;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Builder
    public ChatRoomEntity(
            final Long id,
            final LocalDateTime createdAt,
            final LocalDateTime deletedAt
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public static ChatRoomEntity create(
            final LocalDateTime createdAt
    ) {
        return ChatRoomEntity.builder()
                .createdAt(createdAt)
                .build();
    }

}
