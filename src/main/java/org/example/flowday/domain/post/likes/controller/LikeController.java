package org.example.flowday.domain.post.likes.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.flowday.domain.post.likes.dto.LikeResponseDTO;
//import org.example.flowday.domain.post.likes.dto.LikeRequestDTO;
//import org.example.flowday.domain.post.likes.service.LikeService;
//import org.example.flowday.global.security.util.SecurityUser;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/likes")
//@RequiredArgsConstructor
//public class LikeController {
//
//    private final LikeService likeService;
//
//    // 좋아요 추가
//    @PostMapping
//    public ResponseEntity<LikeResponseDTO> addLike(@Valid @RequestBody LikeRequestDTO likeRequestDTO , @AuthenticationPrincipal SecurityUser user) {
//        LikeResponseDTO createdLike = likeService.addLike(likeRequestDTO,user.getId());
//        return new ResponseEntity<>(createdLike, HttpStatus.CREATED);
//    }
//
//    // 좋아요 삭제
//    @DeleteMapping
//    public ResponseEntity<Void> removeLike(@RequestParam Long userId, @RequestParam Long postId) {
//        likeService.removeLike(userId, postId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    // 특정 좋아요 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<LikeResponseDTO> getLikeById(@PathVariable Long id) {
//        Optional<LikeResponseDTO> like = likeService.getLikeById(id);
//        return like.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//}