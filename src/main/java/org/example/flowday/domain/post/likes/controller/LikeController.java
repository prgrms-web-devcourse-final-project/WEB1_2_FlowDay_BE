package org.example.flowday.domain.post.likes.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.likes.dto.LikesDTO;
import org.example.flowday.domain.post.likes.service.LikeService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 좋아요 추가
    @PostMapping("{postId}")
    public ResponseEntity<LikesDTO.LikeResponseDTO> addLike(@PathVariable Long postId , @AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(likeService.addLike(postId, user.getId()));
    }

    // 좋아요 삭제
    @DeleteMapping("{postId}")
    public ResponseEntity<String> removeLike(@PathVariable Long postId , @AuthenticationPrincipal SecurityUser user) {
        likeService.removeLike(postId, user.getId());
        return ResponseEntity.ok().body("좋아요가 삭제되었습니다");

    }
}