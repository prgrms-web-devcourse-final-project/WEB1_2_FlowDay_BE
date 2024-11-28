package org.example.flowday.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.chat.common.ApiResponse;
import org.example.flowday.domain.chat.dto.ChatResponse;
import org.example.flowday.domain.chat.entity.ChatMessageEntity;
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

@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@RestController
public class ChatApiController {
    private final ChatService chatService;

    /**
     * 채팅 방 생성
     * - 연인 수락을 하면 채팅방 id가 생성된다
     */
    @PostMapping
    public ResponseEntity<ApiResponse> registerChatRoom() {
        LocalDateTime time = LocalDateTime.now();
        Long chatRoomId = chatService.registerChatRoom(time);
        return ResponseEntity.ok(ApiResponse.success(chatRoomId));
    }


    /**
     * 채팅 조회 (최신 10개, page)
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse> getPagedChatMessages(
            @PathVariable Long roomId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "sendTime"
        ));
        Page<ChatMessageEntity> messages = chatService.getPagedChatMessages(roomId, pageable);
        Page<ChatResponse> chatResponses = messages.map(ChatResponse::from);
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
