package org.example.flowday.domain.notification.mapper;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.notification.dto.NotificationRequestDTO;
import org.example.flowday.domain.notification.dto.NotificationResponseDTO;
import org.example.flowday.domain.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    private final MemberRepository memberRepository;

    public NotificationMapper(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * NotificationRequestDTO -> Notification Entity 변환
     */
    public Notification toEntity(NotificationRequestDTO dto) {
        // senderId와 receiverId를 Member 객체로 조회
        Member sender = memberRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 senderId"));
        Member receiver = memberRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 receiverId"));

        return Notification.builder()
                .senderId(sender)  // Member 객체로 설정
                .receiverId(receiver)  // Member 객체로 설정
                .message(dto.getMessage())
                .url(dto.getUrl())
                .isRead(false)
                .additionalParamsJson(dto.getParams() != null ? dto.getParams().toString() : null)  // params를 JSON으로 저장
                .build();
    }

    /**
     * Notification Entity -> NotificationResponseDTO 변환
     */
    public NotificationResponseDTO toResponseDTO(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .senderId(notification.getSenderId().getId())  // senderId는 Member 객체이므로 ID만 가져옴
                .receiverId(notification.getReceiverId().getId())  // receiverId도 마찬가지
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .additionalParamsJson(notification.getAdditionalParamsJson())
                .build();
    }
}
