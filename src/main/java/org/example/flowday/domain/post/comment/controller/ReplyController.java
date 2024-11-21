package org.example.flowday.domain.post.comment.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.comment.dto.ReplyDTO;
import org.example.flowday.domain.post.comment.service.ReplyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<ReplyDTO.Response>> getRepliesByPostId(@PathVariable Long postId) {
        List<ReplyDTO.Response> replies = replyService.findAllByPost(postId);
        return ResponseEntity.ok(replies);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<ReplyDTO.createResponse> createReply(@RequestBody ReplyDTO.createRequest request , @PathVariable Long postId ,@RequestParam Long memberId ) {
        ReplyDTO.createResponse response = replyService.saveReply(request, memberId, postId);

        return ResponseEntity.status(CREATED).body(response);

    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<ReplyDTO.updateResponse> updateReply(@RequestBody ReplyDTO.updateRequest request, @PathVariable Long replyId) {
        ReplyDTO.updateResponse response = replyService.updateReply(request, replyId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyId) {
        replyService.removeReply(replyId);

        return ResponseEntity.ok("댓글이 삭제되었습니다 ");
    }



}
