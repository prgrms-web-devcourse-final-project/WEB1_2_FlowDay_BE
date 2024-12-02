package org.example.flowday.domain.notification.controller;

import lombok.RequiredArgsConstructor;

import org.example.flowday.domain.notification.dto.NotificationDTO;
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

    /**
     * 알림 생성
     */
    @PostMapping("/")
    public ResponseEntity<NotificationDTO.NotificationResponseDTO> createNotification(@RequestBody NotificationDTO.NotificationRequestDTO notificationRequestDTO) {
        NotificationDTO.NotificationResponseDTO responseDTO = notificationService.createNotification(notificationRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 알림 조회 (ID로 조회)
     */
    @GetMapping("/")
    public ResponseEntity<NotificationDTO.NotificationResponseDTO> getNotification(@AuthenticationPrincipal SecurityUser user) {
        Optional<NotificationDTO.NotificationResponseDTO> notification = notificationService.getNotificationById(user.getId());
        return notification.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // receiverId에 해당하는 알림을 페이징 처리하여 반환하는 API
    @GetMapping("/notifications")
    public Page<NotificationDTO.NotificationResponseDTO> getNotifications(
            @RequestParam Long receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return notificationService.getNotificationsByReceiverId(receiverId, page, size);
    }

    /**
     * 알림 읽음 처리
     */
    @PutMapping("/read/{id}")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Marked as read");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Deleted notification");
    }
}


