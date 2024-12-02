package org.example.flowday.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.notification.dto.NotificationDTO;
import org.example.flowday.domain.notification.entity.Notification;
import org.example.flowday.domain.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 알림 생성
     */
    public NotificationDTO.NotificationResponseDTO createNotification(NotificationDTO.NotificationRequestDTO dto) throws JsonProcessingException {

        System.out.println(dto.toString());

        Notification notify = dto.toEntity();
        notify.setAdditionalParams(dto.getParams());

        Notification notification = notificationRepository.save(notify);
        simpMessagingTemplate.convertAndSend("/topic/notifications/" + dto.getReceiverId(), new NotificationDTO.CreateResponseDTO(notification.getReceiverId()));

        return convertToResponseDTO(notify);
    }

    /**
     * 알림 조회 (ID로 조회)
     */
    public Optional<NotificationDTO.NotificationResponseDTO> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::convertToResponseDTO);
    }

    // receiverId에 해당하는 알림을 페이징 처리하여 가져오는 메서드
    public Page<NotificationDTO.NotificationResponseDTO> getNotificationsByReceiverId(Long receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // 페이지 번호와 페이지 크기
        Page<Notification> notifications = notificationRepository.findByReceiverId(receiverId, pageable);
        return notifications.map(this::convertToResponseDTO);
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsRead(Long id) {
        notificationRepository.updateIsRead(id);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    /**
     * 알림 응답 DTO로 변환
     */
    private NotificationDTO.NotificationResponseDTO convertToResponseDTO(Notification notification) {
        NotificationDTO.NotificationResponseDTO responseDTO = new NotificationDTO.NotificationResponseDTO();
        responseDTO.setId(notification.getId());
        responseDTO.setReceiverId(notification.getReceiverId());
        responseDTO.setSenderId(notification.getSenderId());
        responseDTO.setMessage(notification.getMessage());
        responseDTO.setIsRead(notification.getIsRead());
        responseDTO.setUrl(notification.getUrl());
        responseDTO.setCreatedAt(notification.getCreatedAt());
        responseDTO.setAdditionalParamsJson(notification.getAdditionalParamsJson());
        return responseDTO;
    }
}
