package org.example.flowday.domain.post.comment.comment.controller;

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
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<ReplyDTO.Response>> getRepliesByPostId(@PathVariable Long postId) {
        List<ReplyDTO.Response> replies = replyService.findAllByPost(postId);
        return ResponseEntity.ok(replies);
    }

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

    @DeleteMapping("/{replyId}")
    public ResponseEntity<ReplyDTO.deleteResponse> deleteReply(@PathVariable Long replyId , @AuthenticationPrincipal SecurityUser user) {
        ReplyDTO.deleteResponse response = replyService.removeReply(replyId, user.getId());

        return ResponseEntity.ok(response);
    }



}
