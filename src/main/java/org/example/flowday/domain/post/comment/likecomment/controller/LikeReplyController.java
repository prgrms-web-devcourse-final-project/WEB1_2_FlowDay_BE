package org.example.flowday.domain.post.comment.likecomment.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.comment.likecomment.service.LikeReplyService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeReplyController {
    private final LikeReplyService likeReplyService;


    @PostMapping("/{replyId}")
    public ResponseEntity<String> createLikeReply(@PathVariable Long replyId , @AuthenticationPrincipal SecurityUser user) {
        likeReplyService.saveLikeReply(user.getId() , replyId);

        return ResponseEntity.ok("좋아요가 생성되었습니다");

    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteLikeReply(@PathVariable Long replyId , @AuthenticationPrincipal SecurityUser user ) {
        likeReplyService.removeLikeReply(user.getId() , replyId);

        return ResponseEntity.ok("좋아요가 삭제되었습니다");

    }


}
