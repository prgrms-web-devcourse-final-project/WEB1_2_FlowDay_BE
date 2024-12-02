package org.example.flowday.domain.notification.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
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

    private Long senderId;
    private Long receiverId;
    private String message;
    private Boolean isRead;
    private String url;

    @CreatedDate
    private LocalDateTime createdAt;

    @Lob
    private String additionalParamsJson;

    // JSON 직렬화 및 역직렬화
    public void setAdditionalParams(Map<String, Object> params) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.additionalParamsJson = objectMapper.writeValueAsString(params); // Map을 JSON 문자열로 변환하여 저장
    }

    public Map<String, Object> getAdditionalParams() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(this.additionalParamsJson, Map.class); // JSON 문자열을 Map으로 역직렬화
    }
}
