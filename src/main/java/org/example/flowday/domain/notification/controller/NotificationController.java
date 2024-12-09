package org.example.flowday.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.notification.dto.NotificationRequestDTO;
import org.example.flowday.domain.notification.dto.NotificationResponseDTO;
import org.example.flowday.domain.notification.service.NotificationService;
import org.example.flowday.global.security.util.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    /**
     * 알림 생성
     * - 클라이언트에서 알림 생성 요처을 보내면, 알림을 생성하고 응답을 반환
     * - NotificationRequestDTO 객체에 담긴 정보를 바탕으로 알림을 생성함
     */
    @PostMapping("/")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @RequestBody NotificationRequestDTO notificationRequestDTO) {

        try {
            // 알림 생성 서비스 호출
            NotificationResponseDTO responseDTO = notificationService.createNotification(notificationRequestDTO);
            logger.info("알림 생성 성공 : 알림 ID {}", responseDTO.getId());

            // 생성된 알림을 응답으로 반환
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("알림 생성 실패 : {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);            // 서버 오류 응답
        }
    }

    /**
     * 알림 조회 (ID로 조회)
     * - 알림의 ID를 통해 특정 알림을 조회하고 결과를 반환
     * - 알림이 없으면 404 Not Found 응답을 반환
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotification(
            @PathVariable Long id) {

        try {
            Optional<NotificationResponseDTO> notification = notificationService.getNotificationById(id);

            // 알림을 찾은 경우 200 응답, 없으면 404 응답
            if (notification.isPresent()) {
                logger.info("알림 조회 성공 : 알림 ID {}", id);
                return ResponseEntity.ok(notification.get());
            } else {
                logger.warn("알림 조회 실패 - 알림 Id {}를 찾을 수 없음", id);
                return ResponseEntity.notFound().build();           // 알림이 없으면 404
            }
        } catch (Exception e) {
            logger.error("알림 조회 실패 : 알림 ID {}", id, e);
            return ResponseEntity.status(500).body(null);           // 서버 오류 응답
        }

    }

    /**
     * 특정 사용자의 알림을 페이징 처리하여 반환하는 API
     * - 로그인된 사용자(SecurityUser)의 알림을 페이징 처리해서 반환
     * - 페이지 번호와 크기는 0과 10으로 설정
     */
    @GetMapping("/all")
    public ResponseEntity<Page<NotificationResponseDTO>> getNotifications(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // SecurityUser에서 Member 객체를 가져옵니다.
            Member receiver = user.member();

            // 알림 목록
            Page<NotificationResponseDTO> notifications = notificationService.getNotificationsByReceiver(receiver, page, size);
            logger.info("알림 목록 조회 성공 : 사용자 ID {}, 페이지 {}, 알림 개수 : {}", receiver.getId(), page, notifications.getSize());

            return ResponseEntity.ok(notifications);            // 알림 목록 반환
        } catch (Exception e) {
            logger.error("알림 목록 조회 실패 : 사용자 ID {}", user.getId(), e);
            return ResponseEntity.status(500).body(null);       // 서버 오류 응답
        }
    }

    /**
     * 알림 읽음 처리
     * - 알림을 '읽음' 상태로 처리
     * - 해당 알림의 ID를 받아서 알림 상태로 변경
     */
    @PutMapping("/read/{id}")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {

        try {
            // 알림을 읽음 처리하는 서비스 호출
            notificationService.markAsRead(id);
            logger.info("알림 읽음 처리 성공 : 알림 ID {}", id);
            return ResponseEntity.ok("읽음 처리되었습니다.");

        } catch (Exception e) {
            logger.error("알림 읽음 처리 실패 : 알림 ID {}", id, e);
            return ResponseEntity.status(500).body("읽음 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 알림 삭제
     * - 알림을 삭제
     * - 알림 ID를 받아서 해당 알림을 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {

        try {
            // 알림 삭제 서비스 호출
            notificationService.deleteNotification(id);
            logger.info("알림 삭제 성공 : 알림 ID {}", id);
            return ResponseEntity.ok("알림이 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("알림 삭제 실패 : 알림 ID {}", id, e);
            return ResponseEntity.status(500).body("알림 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * 좋아요 알림 생성
     * - 사용자가 게시물에 좋아요를 누르면 해당 게시물 작성자에게 알림을 전송함
     * - 발신자와 수신자 정보가 필요하고, 알림을 생성하고 해당 수신자에게 전송함
     */
    @PostMapping("/like")
    public ResponseEntity<String> sendLikeNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long postId,
            @RequestParam Long receiverId) {

        try {
            // 발신자(sender)는 로그인한 사용자 (좋아요를 누른 사람)
            Member sender = user.member();

            // 수신자(receiver)는 receiverId로 찾은 게시물 작성자
            Member receiver = memberRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

            // 좋아요 알림 생성
            notificationService.sendLikeNotification(sender, postId, receiver);
            logger.info("좋아요 알림 생성 성공 : 발신자 ID {}, 게시물 ID {}, 수신자 ID {}", sender.getId(), postId, receiver.getId());
            return ResponseEntity.ok("좋아요 알림이 전송되었습니다.");
        } catch (Exception e) {
            logger.error("좋아요 알림 생성 실패 : 게시물 ID {}, 수신자 ID {}", postId, receiverId, e);
            return ResponseEntity.status(500).body("좋아요 알림 전송 중 오류가 발생했습니다.");
        }
    }

    /**
     * 댓글 알림 생성
     * - 사용자가 게시물에 댓글을 달면 해당 게시물 작성자에게 알림을 전송함
     * - 발신자와 수신자 정보가 필요하고 알림을 생성해서 해당 수신자에게 전송함
     */
    @PostMapping("/comment")
    public ResponseEntity<String> sendCommentNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long postId,
            @RequestParam Long receiverId) {

        try {
            // 발신자(sender)는 로그인한 사용자 (댓글을 작성한 사람)
            Member sender = user.member();

            // 수신자(receiver)는 receiverId로 찾은 게시물 작성자
            Member receiver = memberRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

            // 댓글 알림 생성
            notificationService.sendCommentNotification(sender, postId, receiver);
            logger.info("댓글 알림 생성 성공 : 발신자 ID {}, 게시물 ID {}, 수신자 ID {}", sender.getId(), postId, receiver.getId());
            return ResponseEntity.ok("댓글 알림이 전송되었습니다.");
        } catch (Exception e) {
            logger.error("댓글 알림 생성 실패 : 게시물 ID {}, 수신자 ID {}", postId, receiverId, e);
            return ResponseEntity.status(500).body("댓글 알림 전송 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코스 공유 알림 생성
     * - 사용자가 코스를 공유하면 수신자에게 알림을 전송함
     * - 발신자와 수신자 정보가 필요하고 알림을 생상하고 수신자에게 전송함
     */
    @PostMapping("/course/share")
    public ResponseEntity<String> sendCourseShareNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long courseId,
            @RequestParam Long receiverId) {

        try {
            // 발신자(sender)는 로그인한 사용자 (코스를 공유한 사람)
            Member sender = user.member();

            // 수신자(receiver)는 receiverId로 찾은 사용자
            Member receiver = memberRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

            // 코스 공유 알림 생성
            notificationService.sendCourseShareNotification(sender, courseId, receiver);
            logger.info("코스 공유 알림 생성 성공 : 발신자 ID {}, 코스 ID {}, 수신자 ID {}", sender.getId(), courseId, receiver.getId());
            return ResponseEntity.ok("코스 공유 알림이 전송되었습니다.");
        } catch (Exception e) {
            logger.error("코스 공유 알림 생성 실패 : 코스 ID {}, 수신자 ID {}", courseId, receiverId, e);
            return ResponseEntity.status(500).body("코스 공유 알림 전송 중 오류가 발생했습니다.");
        }
    }

    /**
     * 커플 신청 알림 생성
     * - 사용자가 커플 신청을 할 때 수신자한테 알림을 전송함
     * - 발신자와 수신자 정보가 필요하고 알림을 생성하고 수신자에게 전송함
     */
    @PostMapping("/couple/request")
    public ResponseEntity<String> sendCoupleRequestNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long receiverId) {

        try {
            // 발신자(sender)는 로그인한 사용자 (커플 신청한 사람)
            Member sender = user.member();

            // 수신자(receiver)는 receiverId로 찾은 사용자
            Member receiver = memberRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

            // 커플 신청 알림 생성
            notificationService.sendCoupleRequestNotification(sender, receiver);
            logger.info("커플 신청 알림 생성 성공 : 발신자 ID {}, 수신자 ID {}", sender.getId(), receiver.getId());
            return ResponseEntity.ok("커플 신청 알림이 전송되었습니다.");
        } catch (Exception e) {
            logger.error("커플 신청 알림 생성 실패 : 수신자 ID {}", receiverId, e);
            return ResponseEntity.status(500).body("커플 신청 알림 전송 중 오류가 발생했습니다.");
        }
    }

    /**
     * 커플 끊어짐 알림 생성
     * - 사용자가 커플 관계가 끊어졌을 때 수신자에게 알림을 전송함
     * - 발신자와 수신자 정보가 필요하고 알림을 생성하고 수신자에게 전송함
     */
    @PostMapping("/couple/disconnect")
    public ResponseEntity<String> sendCoupleDisconnectNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long receiverId) {

        try {
            // 발신자(sender)는 로그인한 사용자 (커플이 끊어진 사람)
            Member sender = user.member();

            // 수신자(receiver)는 receiverId로 찾은 사용자
            Member receiver = memberRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

            // 커플 끊어짐 알림 생성
            notificationService.sendCoupleDisconnectNotification(sender, receiver);
            logger.info("커플 끊어짐 알림 생성 성공 : 발신자 ID {}, 수신자 ID {}", sender.getId(), receiver.getId());
            return ResponseEntity.ok("커플이 끊어졌습니다.");
        } catch (Exception e) {
            logger.error("커플 끊어짐 알림 생성 실패 : 수신자 ID {}", receiverId, e);
            return ResponseEntity.status(500).body("커플 끊어짐 알림 전송 중 오류가 발생했습니다.");
        }
    }

    /**
     * 투표 요청 알림 생성
     * - 사용자가 투표를 요청할 때 수신자에게 알림을 전송함
     * - 발신자와 수신자 정보가 필요하고 알림을 생성하고 수신자에게 전송함
     */
    @PostMapping("/vote/request")
    public ResponseEntity<String> sendVoteRequestNotification(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam Long voteId,
            @RequestParam Long receiverId) {

        try {
            // 발신자(sender)는 로그인한 사용자 (투표 요청을 보낸 사람)
            Member sender = user.member();

            // 수신자(receiver)는 receiverId로 찾은 사용자
            Member receiver = memberRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

            // 투표 요청 알림 생성
            notificationService.sendVoteRequestNotification(sender, voteId, receiver);
            logger.info("투표 요청 알림 생성 성공 : 발신자 ID {}, 투표 ID {}, 수신자 ID {}", sender.getId(), voteId, receiver.getId());
            return ResponseEntity.ok("투표 요청 알림이 전송되었습니다.");
        } catch (Exception e) {
            logger.error("투표 요청 알림 생성 실패 : 투표 ID {}, 수신자 ID {}", voteId, receiverId, e);
            return ResponseEntity.status(500).body("투표 요청 알림 전송 중 오류가 발생했습니다.");
        }
    }
}
