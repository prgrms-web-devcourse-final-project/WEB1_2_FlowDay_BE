package org.example.flowday.domain.post.post.controller;

import ch.qos.logback.core.model.Model;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.flowday.domain.post.post.dto.PostBriefResponseDTO;
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.post.service.PostService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPost(@Valid @ModelAttribute PostRequestDTO postRequestDto, @AuthenticationPrincipal SecurityUser user) {
        PostResponseDTO createdPost = postService.createPost(postRequestDto, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 작성되었습니다");
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        PostResponseDTO result = postService.getPostById(id);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 모든 게시글 최신순 조회
    @GetMapping("/all/latest")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.getAllPosts(pageable);

        return ResponseEntity.ok().body(result);

    }

    // 모든 게시글 인기순 조회
    @GetMapping("/all/mostLike")
    public ResponseEntity<Page<PostBriefResponseDTO>> getMostPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.getAllPosts(pageable);

        return ResponseEntity.ok().body(result);

    }

    //커플 게시글 조회
    @GetMapping("/all/couple")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllCouplePosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                                        @AuthenticationPrincipal SecurityUser user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.findAllCouplePosts(pageable, user.getId());

        return ResponseEntity.ok().body(result);
    }

    // Private 조건의 리스트 조회
    @GetMapping("/all/private")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllPrivatePosts( @RequestParam(defaultValue = "0") int page , @RequestParam(defaultValue = "20") int pageSize,
                                                                          @AuthenticationPrincipal SecurityUser user) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> results = postService.findAllPrivate(pageable, user.getId());

        return ResponseEntity.ok().body(results);

    }

    //나의 게시글 조회
    @GetMapping("/all")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllMyPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                                        @AuthenticationPrincipal SecurityUser user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.findAllMyPosts(pageable, user.getId());

        return ResponseEntity.ok().body(result);
    }

    //내가 좋아요 누른 게시글 조회
    @GetMapping("/all/likes")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllMyLikesPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                                        @AuthenticationPrincipal SecurityUser user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.findAllMyLikePosts(pageable, user.getId());

        return ResponseEntity.ok().body(result);
    }

    //내가 댓글 단 게시글 조회
    @GetMapping("/all/reply")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllMyReplyPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                                         @AuthenticationPrincipal SecurityUser user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.findAllMyReplyPosts(pageable, user.getId());

        return ResponseEntity.ok().body(result);
    }


    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @Valid @ModelAttribute PostRequestDTO updatedPostDto ,@AuthenticationPrincipal SecurityUser user) {
        PostResponseDTO post = postService.updatePost(id, updatedPostDto, user.getId());
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id , @AuthenticationPrincipal SecurityUser user) {
        postService.deletePost(id,user.getId());
        return ResponseEntity.ok().body("게시글이 삭제되었습니다");
    }


}