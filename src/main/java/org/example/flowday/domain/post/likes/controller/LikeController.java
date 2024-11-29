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
@RequestMapping("/api/v1/posts/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 좋아요 추가
    @PostMapping
    public ResponseEntity<LikesDTO.LikeResponseDTO> addLike(@RequestBody LikesDTO.LikeRequestDTO likeRequestDTO , @AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(likeService.addLike(likeRequestDTO, user.getId()));
    }

    // 좋아요 삭제
    @DeleteMapping
    public ResponseEntity<String> removeLike(@RequestBody LikesDTO.LikeRequestDTO likeRequestDTO , @AuthenticationPrincipal SecurityUser user) {
        likeService.removeLike(likeRequestDTO, user.getId());
        return ResponseEntity.ok().body("좋아요가 삭제되었습니다");

    }
}