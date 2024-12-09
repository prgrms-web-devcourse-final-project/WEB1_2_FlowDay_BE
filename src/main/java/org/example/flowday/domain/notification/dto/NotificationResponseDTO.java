package org.example.flowday.domain.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDTO {
    private Long id;           // 알림 ID
    private Long senderId;     // 보낸 사람의 ID
    private Long receiverId;   // 받는 사람의 ID
    private String message;    // 알림 메시지
    private Boolean isRead;    // 읽음 여부
    private String url;        // 실행할 URL
    private String type;
    private LocalDateTime createdAt;  // 생성 일시
    private String additionalParamsJson;  // 추가 파라미터


    public static class CreateResponseDTO {
        private Long receiverId;
        private String url = "/api/v1/notifications/all";

        public CreateResponseDTO(Long id) {
            this.receiverId = id;
        }
    }
}