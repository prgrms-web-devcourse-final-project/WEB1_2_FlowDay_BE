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

    @Operation(summary = "조회")
    @GetMapping("/{postId}")
    public ResponseEntity<List<ReplyDTO.Response>> getRepliesByPostId(@PathVariable Long postId) {
        List<ReplyDTO.Response> replies = replyService.findAllByPost(postId);
        return ResponseEntity.ok(replies);
    }

    @Operation(summary = "작성")
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

    @Operation(summary = "수정")
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

    @Operation(summary = "삭제")
    @DeleteMapping("/{replyId}")
    public ResponseEntity<ReplyDTO.deleteResponse> deleteReply(@PathVariable Long replyId , @AuthenticationPrincipal SecurityUser user) {
        ReplyDTO.deleteResponse response = replyService.removeReply(replyId, user.getId());

        return ResponseEntity.ok(response);
    }



}
