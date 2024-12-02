package org.example.flowday.domain.post.comment.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.comment.comment.dto.ReplyDTO;
import org.example.flowday.domain.post.comment.comment.service.ReplyService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/replies")
@Tag(name = "Reply", description = "댓글 관련 api")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    @Operation(summary ="댓글 전체 조회"  , description = "게시글의 전체 댓글을 조회합니다")
    @GetMapping("/{postId}")
    public ResponseEntity<List<ReplyDTO.Response>> getRepliesByPostId(@PathVariable Long postId) {
        List<ReplyDTO.Response> replies = replyService.findAllByPost(postId);
        return ResponseEntity.ok(replies);
    }

    @Operation(summary ="댓글 생성"  , description = "부모 댓글 생성시 parentId에 null 값을 넣어주세요 , 자식 댓글 생성시 부모댓글의 id값을 parent에 넣어주세요  ")
    @PostMapping("/{postId}")
    public ResponseEntity<?> createReply(@RequestBody @Valid ReplyDTO.createRequest request , BindingResult bindingResult, @PathVariable Long postId,
                                         @AuthenticationPrincipal SecurityUser user) {
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        ReplyDTO.createResponse response = replyService.saveReply(request, user.getId(), postId);

        return ResponseEntity.status(CREATED).body(response);

    }

    @Operation(summary ="댓글 수정"  , description = "댓글이 수정 됩니다")
    @PatchMapping("/{replyId}")
    public ResponseEntity<?> updateReply(@RequestBody @Valid ReplyDTO.updateRequest request,BindingResult bindingResult , @PathVariable Long replyId ,
                                         @AuthenticationPrincipal SecurityUser user) {
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        ReplyDTO.updateResponse response = replyService.updateReply(request, replyId,user.getId());

        return ResponseEntity.ok(response);
    }

    @Operation(summary ="댓글 삭제"  , description = "자식댓글은 삭제되지만 부모 댓글은 삭제되지 않고 '작성자에 의해 삭제되었습니다'로 내용이 변경됩니다")
    @DeleteMapping("/{replyId}")
    public ResponseEntity<ReplyDTO.deleteResponse> deleteReply(@PathVariable Long replyId , @AuthenticationPrincipal SecurityUser user) {
        ReplyDTO.deleteResponse response = replyService.removeReply(replyId, user.getId());

        return ResponseEntity.ok(response);
    }



}
