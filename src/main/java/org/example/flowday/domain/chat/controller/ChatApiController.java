package org.example.flowday.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.common.ApiResponse;
import org.example.flowday.domain.chat.dto.ChatResponse;
import org.example.flowday.domain.chat.entity.ChatMessageDocument;
import org.example.flowday.domain.chat.service.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@RestController
public class ChatApiController {
    private final ChatService chatService;

    /**
     * 채팅 방 생성
     * - 연인 수락을 하면 채팅방 id가 생성된다
     */
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse> registerChatRoom() {
        LocalDateTime time = LocalDateTime.now();
        Long chatRoomId = chatService.registerChatRoom(time);
        return ResponseEntity.ok(ApiResponse.success(chatRoomId));
    }

    /**
     * [NoSQL] 채팅 조회 (최신 10개, page)
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse> getPagedChatMessages(
            @PathVariable Long roomId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "sendTime"));
        Page<ChatMessageDocument> messages = chatService.getPagedChatMessages(roomId, pageable);

        int pageNumber = messages.getNumber() + 1;
        List<ChatResponse> chatResponses = messages.getContent().stream()
                .map(message -> ChatResponse.from(message, pageNumber, messages.getTotalPages()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(chatResponses));
    }

    /**
     * 채팅 방 삭제
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse> deleteChatRoom(@PathVariable Long roomId) {
        Long deletedChatRoomId = chatService.deleteChatRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success(deletedChatRoomId));
    }
}
