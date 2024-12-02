package org.example.flowday.domain.notification.dto;

import lombok.Builder;
import lombok.Data;
import org.example.flowday.domain.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.Map;

public class NotificationDTO {

    @Data
    public static class NotificationRequestDTO {
        private Long senderId;     // 보낸 사람의 ID
        private Long receiverId;   // 받는 사람의 ID
        private String message;    // 알림 메시지
        private String url;        // 실행할 URL
        private Map<String,Object> params;

        public Notification toEntity() {
            return Notification.builder()
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .message(message)
                    .url(url)
                    .isRead(false)
                    .additionalParams(params)
                    .build();
        }
    }

    @Data
    public static class NotificationResponseDTO {
        private Long id;           // 알림 ID
        private Long senderId;     // 보낸 사람의 ID
        private Long receiverId;   // 받는 사람의 ID
        private String message;    // 알림 메시지
        private Boolean isRead;    // 읽음 여부
        private String url;        // 실행할 URL
        private LocalDateTime createdAt;  // 생성 일시
        private String additionalParamsJson;
    }

}
