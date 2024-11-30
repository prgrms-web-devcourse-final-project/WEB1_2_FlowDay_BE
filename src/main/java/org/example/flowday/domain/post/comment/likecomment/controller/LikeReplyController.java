package org.example.flowday.domain.post.comment.likecomment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.comment.likecomment.service.LikeReplyService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes/replies")
@Tag(name = "Like", description = "좋아요 관련 api")
public class LikeReplyController {
    private final LikeReplyService likeReplyService;

    @Operation(summary = "생성")
    @PostMapping("/{replyId}")
    public ResponseEntity<String> createLikeReply(@PathVariable Long replyId , @AuthenticationPrincipal SecurityUser user) {
        likeReplyService.saveLikeReply(user.getId() , replyId);

        return ResponseEntity.ok("좋아요가 생성되었습니다");

    }

    @Operation(summary = "삭제")
    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteLikeReply(@PathVariable Long replyId , @AuthenticationPrincipal SecurityUser user ) {
        likeReplyService.removeLikeReply(user.getId() , replyId);

        return ResponseEntity.ok("좋아요가 삭제되었습니다");

    }


}
