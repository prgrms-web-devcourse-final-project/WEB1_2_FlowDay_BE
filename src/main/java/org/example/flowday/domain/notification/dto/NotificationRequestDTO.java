package org.example.flowday.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {
    private Long senderId;      // 보낸 사람의 ID
    private Long receiverId;    // 받는 사람의 ID
    private String message;     // 알림 메시지
    private String url;         // 실행할 URL
    private Map<String, Object> params;     // 추가 파라미터

}