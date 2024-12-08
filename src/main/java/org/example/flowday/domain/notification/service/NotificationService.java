package org.example.flowday.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.notification.dto.NotificationRequestDTO;
import org.example.flowday.domain.notification.dto.NotificationResponseDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface NotificationService {

    /**
     * 알림 생성
     */
    NotificationResponseDTO createNotification(NotificationRequestDTO dto) throws JsonProcessingException;

    /**
     * 알림 조회 (ID로 조회)
     */
    Optional<NotificationResponseDTO> getNotificationById(Long id);

    /**
     * receiver에 해당하는 알림을 페이징 처리하여 가져오기
     */
    Page<NotificationResponseDTO> getNotificationsByReceiver(Member receiver, int page, int size);

    /**
     * 알림 읽음 처리
     */
    void markAsRead(Long id);

    /**
     * 알림 삭제
     */
    void deleteNotification(Long id);

    /**
     * 좋아요 알림 생성
     */
    void sendLikeNotification(Member sender, Long postId, Member receiver);

    /**
     * 댓글 알림 생성
     */
    void sendCommentNotification(Member sender, Long postId, Member receiver);

    /**
     * 코스 공유 알림 생성
     */
    void sendCourseShareNotification(Member sender, Long courseId, Member receiver);

    /**
     * 커플 신청 알림 생성
     */
    void sendCoupleRequestNotification(Member sender, Member receiver);

    /**
     * 커플 끊어짐 알림 생성
     */
    void sendCoupleDisconnectNotification(Member sender, Member receiver);

    /**
     * 투표 요청 알림 생성
     */
    void sendVoteRequestNotification(Member sender, Long voteId, Member receiver);
}