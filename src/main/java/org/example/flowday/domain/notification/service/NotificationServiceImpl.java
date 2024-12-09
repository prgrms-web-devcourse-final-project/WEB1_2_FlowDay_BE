package org.example.flowday.domain.notification.service;

import java.util.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Autowired
    private  SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationMapper notificationMapper;

    // getMemberById 메서드 로깅 추가
    public Member getMemberById(Long memberId) {
        logger.info("사용자 조회 시작 : 사용자 ID {}", memberId);

        // 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    logger.error("사용자 조회 실패 : 사용자 ID {}를 찾을 수 없습니다.", memberId);
                    return new RuntimeException("사용자를 찾을 수 없습니다. 사용자 ID: " + memberId);
                });

        logger.info("사용자 조회 성공 : 사용자 ID {}, 사용자 이름 {}", memberId, member.getName());
        return member;
    }


    /**
     * 알림 생성
     */
    @Override
    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO dto) {
        // Notification 엔티티로 변환
        Notification notify = notificationMapper.toEntity(dto);
        // 추가 파라미터 설정
        notify.setAdditionalParams(dto.getParams());

        // 알림 저장
        Notification notification = notificationRepository.save(notify);

        // WebSocket을 통해 클라이언트로 알림 전송
        try {
            sendWebSocketNotification(dto.getReceiverId(), notification);
        } catch (Exception e) {
            logger.error("WebSocket 알림 전송 실패 : 수신자 {}에게 알림 전송 중 오류 발생", dto.getReceiverId());
        }
        return notificationMapper.toResponseDTO(notification);
    }

    // WebSocket 알림 전송 메서드
    @Async
    public void sendWebSocketNotification(Long receiverId, Notification notification) {
        try {
            NotificationResponseDTO responseDTO = notificationMapper.toResponseDTO(notification);
            responseDTO.setType(notification.getType());
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + receiverId, responseDTO);
            logger.info("알림을 성공적으로 수신자 {}에게 전송하였습니다.", receiverId);
        } catch (Exception e) {
            logger.error("WebSocket 알림 전송 실패: 수신자 {}에게 알림 전송 중 오류 발생", receiverId, e);
        }
    }

    // 알림 타입별 공통 메서드
    private void sendNotification(Member sender, Member receiver, String message, String url, Map<String, Object> params, String type) {
        NotificationRequestDTO requestDTO = NotificationRequestDTO.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .message(message)
                .url(url)
                .params(params)
                .type(type)
                .build();
        createNotification(requestDTO);
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
        String type = "LIKE";
        sendNotification(sender, receiver, message, "/post/" + postId, Map.of("postId", postId, "senderId", sender.getId()), type);
    }

    /**
     * 댓글 알림 생성
     */
    @Override
    public void sendCommentNotification(Member sender, Long postId, Member receiver) {
        String message = sender.getName() + "님이 게시글에 댓글을 작성하였습니다.";
        String type = "COMMENT";
        sendNotification(sender, receiver, message, "/post/" + postId, Map.of("postId", postId, "senderId", sender.getId()), type);
    }

    /**
     * 코스 공유 알림 생성
     */
    @Override
    public void sendCourseShareNotification(Member sender, Long courseId, Member receiver) {
        String message = sender.getName() + "님이 코스를 공유했습니다.";
        String type = "COURSE";
        sendNotification(sender, receiver, message, "/course/" + courseId, Map.of("courseId", courseId, "senderId", sender.getId()), type);
    }

    /**
     * 커플 신청 알림 생성
     */
    @Override
    public void sendCoupleRequestNotification(Member sender, Member receiver) {
        String message = sender.getName() + "님의 커플 신청이 있습니다.";
        String type = "COUPLE";
        sendNotification(sender, receiver, message, "/couple/request", Map.of("senderId", sender.getId()), type);
    }

    /**
     * 커플 끊어짐 알림 생성
     */
    @Override
    public void sendCoupleDisconnectNotification(Member sender, Member receiver) {
        String message = sender.getName() + "님과의 커플 관계가 끊어졌습니다.";
        String type = "COUPLE";
        sendNotification(sender, receiver, message, "/couple/disconnect", Map.of("senderId", sender.getId()), type);
    }

    /**
     * 투표 요청 알림
     */
    @Override
    public void sendVoteRequestNotification(Member sender, Long voteId, Member receiver) {
        String message = sender.getName() + "님이 투표를 요청했습니다.";
        String type = "VOTE";
        sendNotification(sender, receiver, message, "/vote/request" + voteId, Map.of("voteId", voteId, "senderId", sender.getId()), type);
    }
}