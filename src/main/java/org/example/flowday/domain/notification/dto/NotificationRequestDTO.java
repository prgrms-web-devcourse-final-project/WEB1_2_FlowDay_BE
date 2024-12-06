package org.example.flowday.domain.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class NotificationRequestDTO {
    private Long senderId;      // 보낸 사람의 ID
    private Long receiverId;    // 받는 사람의 ID
    private String message;     // 알림 메시지
    private String url;         // 실행할 URL
    private Map<String, Object> params;     // 추가 파라미터

}
