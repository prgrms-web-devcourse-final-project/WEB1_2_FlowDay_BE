package org.example.flowday.domain.post.comment.likecomment.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.comment.likecomment.service.LikeReplyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeReplyController {
    private final LikeReplyService likeReplyService;


    @PostMapping("/{replyId}")
    public ResponseEntity<String> createLikeReply(@PathVariable Long replyId , @RequestParam Long memberId ) {
        likeReplyService.saveLikeReply(memberId , replyId);

        return ResponseEntity.ok("좋아요가 생성되었습니다");

    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteLikeReply(@PathVariable Long replyId , @RequestParam Long memberId ) {
        likeReplyService.removeLikeReply(memberId , replyId);

        return ResponseEntity.ok("좋아요가 삭제되었습니다");

    }


}
