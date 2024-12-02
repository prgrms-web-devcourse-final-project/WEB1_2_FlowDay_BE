package org.example.flowday.domain.post.likes.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.likes.dto.LikesDTO;
import org.example.flowday.domain.post.likes.service.LikeService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/likes/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 좋아요 추가
    @Operation(summary ="게시글 좋아요 추가 "  , description = "이미 좋아요를 눌렀으면 예외가 발생합니다 ")
    @PostMapping("{postId}")
    public ResponseEntity<LikesDTO.LikeResponseDTO> addLike(@PathVariable Long postId , @AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(likeService.addLike(postId, user.getId()));
    }

    // 좋아요 삭제
    @Operation(summary ="게시글 좋아요 삭제 "  , description = "이미 좋아요를 삭제했어도 예외 처리는 발생하지 않으나 게시글의 좋아요는 음수로 내려가지 않습니다 ")
    @DeleteMapping("{postId}")
    public ResponseEntity<String> removeLike(@PathVariable Long postId , @AuthenticationPrincipal SecurityUser user) {
        likeService.removeLike(postId, user.getId());
        return ResponseEntity.ok().body("좋아요가 삭제되었습니다");

    }
}