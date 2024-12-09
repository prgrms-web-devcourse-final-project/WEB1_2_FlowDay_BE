package org.example.flowday.domain.notification.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Member senderId;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiverId;

    private String message;
    private Boolean isRead;
    private String url;
    private String type;

    @CreatedDate
    private LocalDateTime createdAt;

    @Lob
    private String additionalParamsJson;

    // ObjectMapper는 인스턴스화하여 재사용
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // JSON 직렬화 및 역직렬화
    public void setAdditionalParams(Map<String, Object> params) {
        try {
            this.additionalParamsJson = objectMapper.writeValueAsString(params); // Map을 JSON 문자열로 변환하여 저장
        } catch (JsonProcessingException e) {
            throw new RuntimeException("추가 파라미터 직렬화 오류", e); // 예외 처리
        }
    }
}