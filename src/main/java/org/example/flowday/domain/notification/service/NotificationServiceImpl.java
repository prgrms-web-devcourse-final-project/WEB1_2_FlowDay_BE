package org.example.flowday.domain.notification.service;

import java.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.notification.dto.NotificationRequestDTO;
import org.example.flowday.domain.notification.dto.NotificationResponseDTO;
import org.example.flowday.domain.notification.entity.Notification;
import org.example.flowday.domain.notification.exception.ReceiverException;
import org.example.flowday.domain.notification.mapper.NotificationMapper;
import org.example.flowday.domain.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationMapper notificationMapper;

    // 공통된 Member 조회 로직을 처리하는 메서드
    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    logger.error("회원 {}를 찾을 수 없습니다.", memberId);
                    return new ReceiverException("회원 정보를 찾을 수 없습니다.");
                });
    }

    /**
     * 알림 생성
     */
    @Override
    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO dto) throws JsonProcessingException {
        // sender와 receiver 객체 조회
        Member sender = getMemberById(dto.getSenderId());
        Member receiver = getMemberById(dto.getReceiverId());

        // Notification 엔티티로 변환
        Notification notify = notificationMapper.toEntity(dto);
        // 추가 파라미터 설정
        notify.setAdditionalParams(dto.getParams());

        // 알림 저장
        Notification notification = notificationRepository.save(notify);

        // WebSocket을 통해 클라이언트로 알림 전송
        sendWebSocketNotification(dto.getReceiverId(), notification);

        // 알림 응답 DTO 반환
        return notificationMapper.toResponseDTO(notification);
    }

    // WebSocket 알림 전송 메서드
    private void sendWebSocketNotification(Long receiverId, Notification notification) {
        try {
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + receiverId,
                    new NotificationResponseDTO.CreateResponseDTO(notification.getReceiverId().getId()));
            logger.info("알림을 성공적으로 수신자 {}에게 전송하였습니다.", receiverId);
        } catch (Exception e) {
            logger.error("WebSocket 알림 전송 실패: 수신자 {}에게 알림 전송 중 오류 발생", receiverId, e);
            // 실패 시 대체 처리 로직 (예: DB에 저장된 알림을 나중에 재전송)
        }
    }

    // 알림 타입별 공통 메서드
    private void sendNotification(Member sender, Member receiver, String message, String url, Map<String, Object> params) {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .message(message)
                .url(url)
                .params(params)
                .build();

        try {
            createNotification(requestDTO);
        } catch (JsonProcessingException e) {
            logger.error("알림 생성 중 오류 발생", e);
        }
    }

    /**
     * 알림 조회 (ID로 조회)
     */
    @Override
    public Optional<NotificationResponseDTO> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toResponseDTO);
    }

    /**
     * receiver에 해당하는 알림을 페이징 처리하여 가져오기
     */
    @Override
    public Page<NotificationResponseDTO> getNotificationsByReceiver(Member receiver, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationRepository.findByReceiverId(receiver, pageable);
        return notifications.map(notificationMapper::toResponseDTO);
    }

    /**
     * 알림 읽음 처리
     */
    @Override
    public void markAsRead(Long id) {
        notificationRepository.updateIsRead(id);
    }

    /**
     * 알림 삭제
     */
    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    /**
     * 좋아요 알림 생성
     */
    @Override
    public void sendLikeNotification(Member sender, Long postId, Member receiver) {
        String message = sender.getName() + "님이 게시글에 좋아요를 눌렀습니다.";
        sendNotification(sender, receiver, message, "/post/" + postId, Map.of("postId", postId, "senderId", sender.getId()));
    }

    /**
     * 댓글 알림 생성
     */
    @Override
    public void sendCommentNotification(Member sender, Long postId, Member receiver) {
        String message = sender.getName() + "님이 게시글에 댓글을 작성하였습니다.";
        sendNotification(sender, receiver, message, "/post/" + postId, Map.of("postId", postId, "senderId", sender.getId()));
    }

    /**
     * 코스 공유 알림 생성
     */
    @Override
    public void sendCourseShareNotification(Member sender, Long courseId, Member receiver) {
        String message = sender.getName() + "님이 코스를 공유했습니다.";
        sendNotification(sender, receiver, message, "/course/" + courseId, Map.of("courseId", courseId, "senderId", sender.getId()));
    }

    /**
     * 커플 신청 알림 생성
     */
    @Override
    public void sendCoupleRequestNotification(Member sender, Member receiver) {
        String message = sender.getName() + "님의 커플 신청이 있습니다.";
        sendNotification(sender, receiver, message, "/couple/request", Map.of("senderId", sender.getId()));
    }

    /**
     * 커플 끊어짐 알림 생성
     */
    @Override
    public void sendCoupleDisconnectNotification(Member sender, Member receiver) {
        String message = sender.getName() + "님과의 커플 관계가 끊어졌습니다.";
        sendNotification(sender, receiver, message, "/couple/disconnect", Map.of("senderId", sender.getId()));
    }
}
