package org.example.flowday.domain.notification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.notification.dto.NotificationRequestDTO;
import org.example.flowday.domain.notification.dto.NotificationResponseDTO;
import org.example.flowday.domain.notification.service.NotificationService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    /**
     * 알림 생성
     */
    @PostMapping("/")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody NotificationRequestDTO notificationRequestDTO) throws JsonProcessingException {

        // 알림 생성 서비스 호출
        NotificationResponseDTO responseDTO = notificationService.createNotification(notificationRequestDTO);

        // 생성된 알림을 응답으로 반환
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 알림 조회 (ID로 조회)
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotification(
            @PathVariable Long id) {

        Optional<NotificationResponseDTO> notification = notificationService.getNotificationById(id);

        // 알림을 찾은 경우 200 응답, 없으면 404 응답
        return notification.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 특정 사용자의 알림을 페이징 처리하여 반환하는 API
     */
    @GetMapping("/all")
    public Page<NotificationResponseDTO> getNotifications(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // SecurityUser에서 Member 객체를 가져옵니다.
        Member receiver = user.member();

        return notificationService.getNotificationsByReceiver(receiver, page, size);
    }

    /**
     * 알림 읽음 처리
     */
    @PutMapping("/read/{id}")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {

        // 알림을 읽음 처리하는 서비스 호출
        notificationService.markAsRead(id);

        // 성공적인 처리 후 응답
        return ResponseEntity.ok("읽음 처리되었습니다.");
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {

        // 알림 삭제 서비스 호출
        notificationService.deleteNotification(id);

        // 성공적으로 삭제된 후 응답
        return ResponseEntity.ok("알림이 삭제되었습니다.");
    }

    /**
     * 좋아요 알림 생성
     */
    @PostMapping("/like")
    public ResponseEntity<String> sendLikeNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long postId,
            @RequestParam Long receiverId) {

        // 발신자(sender)는 로그인한 사용자 (좋아요를 누른 사람)
        Member sender = user.member();

        // 수신자(receiver)는 receiverId로 찾은 게시물 작성자
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        // 좋아요 알림 생성
        notificationService.sendLikeNotification(sender, postId, receiver);

        return ResponseEntity.ok("좋아요 알림이 전송되었습니다.");
    }

    /**
     * 댓글 알림 생성
     */
    @PostMapping("/comment")
    public ResponseEntity<String> sendCommentNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long postId,
            @RequestParam Long receiverId) {

        // 발신자(sender)는 로그인한 사용자 (댓글을 작성한 사람)
        Member sender = user.member();

        // 수신자(receiver)는 receiverId로 찾은 게시물 작성자
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        // 댓글 알림 생성
        notificationService.sendCommentNotification(sender, postId, receiver);

        return ResponseEntity.ok("댓글 알림이 전송되었습니다.");
    }

    /**
     * 코스 공유 알림 생성
     */
    @PostMapping("/course/share")
    public ResponseEntity<String> sendCourseShareNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long courseId,
            @RequestParam Long receiverId) {

        // 발신자(sender)는 로그인한 사용자 (코스를 공유한 사람)
        Member sender = user.member();

        // 수신자(receiver)는 receiverId로 찾은 사용자
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        // 코스 공유 알림 생성
        notificationService.sendCourseShareNotification(sender, courseId, receiver);

        return ResponseEntity.ok("코스 공유 알림이 전송되었습니다.");
    }

    /**
     * 커플 신청 알림 생성
     */
    @PostMapping("/couple/request")
    public ResponseEntity<String> sendCoupleRequestNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long receiverId) {

        // 발신자(sender)는 로그인한 사용자 (커플 신청한 사람)
        Member sender = user.member();

        // 수신자(receiver)는 receiverId로 찾은 사용자
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        // 커플 신청 알림 생성
        notificationService.sendCoupleRequestNotification(sender, receiver);

        return ResponseEntity.ok("커플 신청 알림이 전송되었습니다.");
    }

    /**
     * 커플 끊어짐 알림 생성
     */
    @PostMapping("/couple/disconnect")
    public ResponseEntity<String> sendCoupleDisconnectNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long receiverId) {

        // 발신자(sender)는 로그인한 사용자 (커플이 끊어진 사람)
        Member sender = user.member();

        // 수신자(receiver)는 receiverId로 찾은 사용자
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        // 커플 끊어짐 알림 생성
        notificationService.sendCoupleDisconnectNotification(sender, receiver);

        return ResponseEntity.ok("커플이 끊어졌습니다.");
    }
}
