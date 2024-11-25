package org.example.flowday.domain.post.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.post.service.PostService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping("")
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO postRequestDto , @AuthenticationPrincipal SecurityUser user) {
        PostResponseDTO createdPost = postService.createPost(postRequestDto,user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id ) {
        PostResponseDTO result = postService.getPostById(id);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 모든 게시글 최신순 조회
    @GetMapping("/all/latest")
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page , int pageSize) {
        Pageable pageable  = PageRequest.of(page, pageSize);
        Page<PostResponseDTO> result = postService.getAllPosts(pageable);

        return ResponseEntity.ok().body(result);

    }

    // 게시글 수정
//    @PutMapping("/{id}")
//    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequestDTO updatedPostDto) {
//        PostResponseDTO post = postService.updatePost(id, updatedPostDto);
//        return new ResponseEntity<>(post, HttpStatus.OK);
//    }

    // 게시글 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
//        postService.deletePost(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}