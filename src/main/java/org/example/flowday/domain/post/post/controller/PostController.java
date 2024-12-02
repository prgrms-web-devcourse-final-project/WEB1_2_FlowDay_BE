package org.example.flowday.domain.post.post.controller;

import ch.qos.logback.core.model.Model;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Post", description = "게시글 관련 api")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 생성 API ",description  ="하나의 코스를 여러 게시글에 등록 할 수 없습니다. 이미지 ,course를 등록하지 않는다면 value 값을 비워서 요청해주세요 ," +
            "content type은 multiPart form으로 요청해주세요 , status는 PRIVATE,PUBLIC,COUPLE 대문자로 요청해주세요 ",
            requestBody = @RequestBody(content=@Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)))
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPost(@Valid @ModelAttribute PostRequestDTO postRequestDto, @AuthenticationPrincipal SecurityUser user) {
        PostResponseDTO createdPost = postService.createPost(postRequestDto, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 작성되었습니다");
    }

    @Operation(summary ="게시글 상세 조회"  , description = "")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        PostResponseDTO result = postService.getPostById(id);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary ="모든 게시글 최신순 조회"  , description = "status=Public으로 설정된 글만 최신순으로 불러옵니다")
    @GetMapping("/all/latest")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.getAllPosts(pageable);

        return ResponseEntity.ok().body(result);

    }

    @Operation(summary ="모든 게시글 인기순 조회"  , description = "status=Public으로 설정된 글만 최신순으로 불러옵니다")
    @GetMapping("/all/mostLike")
    public ResponseEntity<Page<PostBriefResponseDTO>> getMostPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.getAllPosts(pageable);

        return ResponseEntity.ok().body(result);

    }

    @Operation(summary ="커플 게시글 조회"  , description = "status=Couple으로 설정된 본인의 글과 인연의 글을 불러옵니다")
    @GetMapping("/all/couple")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllCouplePosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                                        @AuthenticationPrincipal SecurityUser user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.findAllCouplePosts(pageable, user.getId());

        return ResponseEntity.ok().body(result);
    }

    @Operation(summary ="Private 게시글 조회 "  , description = "status=PRIVATE으로 설정된 본인의 본인의 게시글만 불러옵니다")
    @GetMapping("/all/private")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllPrivatePosts( @RequestParam(defaultValue = "0") int page , @RequestParam(defaultValue = "10") int pageSize,
                                                                          @AuthenticationPrincipal SecurityUser user) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> results = postService.findAllPrivate(pageable, user.getId());

        return ResponseEntity.ok().body(results);

    }

    @Operation(summary ="나의 게시글 조회 "  , description = "내가 작성한 게시글을 불러옵니다.")
    @GetMapping("/all")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllMyPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                                        @AuthenticationPrincipal SecurityUser user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.findAllMyPosts(pageable, user.getId());

        return ResponseEntity.ok().body(result);
    }

    @Operation(summary ="내가 좋아요 누른 게시글 조회 "  , description = "내가 좋아요 누른 게시글을 불러옵니다.")
    @GetMapping("/all/likes")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllMyLikesPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                                        @AuthenticationPrincipal SecurityUser user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.findAllMyLikePosts(pageable, user.getId());

        return ResponseEntity.ok().body(result);
    }

    @Operation(summary ="댓글 단 게시글 조회 "  , description = "내가 댓글 단 게시글을 불러옵니다.")
    @GetMapping("/all/reply")
    public ResponseEntity<Page<PostBriefResponseDTO>> getAllMyReplyPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize,
                                                                         @AuthenticationPrincipal SecurityUser user) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<PostBriefResponseDTO> result = postService.findAllMyReplyPosts(pageable, user.getId());

        return ResponseEntity.ok().body(result);
    }


    @Operation(summary ="게시글 수정 "  , description = "수정 요청한 이미지 파일 그대로 게시글에 재반영됩니다 , 수정 요청의 content type = multipart/form 으로 해주세요")
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @Valid @ModelAttribute PostRequestDTO updatedPostDto ,@AuthenticationPrincipal SecurityUser user) {
        PostResponseDTO post = postService.updatePost(id, updatedPostDto, user.getId());
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @Operation(summary ="게시글 삭제 "  , description = "게시글이 삭제됩니다")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id , @AuthenticationPrincipal SecurityUser user) {
        postService.deletePost(id,user.getId());
        return ResponseEntity.ok().body("게시글이 삭제되었습니다");
    }


}