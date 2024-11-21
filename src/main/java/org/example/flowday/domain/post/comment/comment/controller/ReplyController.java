package org.example.flowday.domain.post.comment.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.comment.comment.dto.ReplyDTO;
import org.example.flowday.domain.post.comment.comment.service.ReplyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createReply(@RequestBody @Valid ReplyDTO.createRequest request , BindingResult bindingResult, @PathVariable Long postId , @RequestParam Long memberId ) {
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        ReplyDTO.createResponse response = replyService.saveReply(request, memberId, postId);

        return ResponseEntity.status(CREATED).body(response);

    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<?> updateReply(@RequestBody @Valid ReplyDTO.updateRequest request,BindingResult bindingResult , @PathVariable Long replyId , @RequestParam Long memberId ) {
        if(bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        ReplyDTO.updateResponse response = replyService.updateReply(request, replyId,memberId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<String> deleteReply(@PathVariable Long replyId , @RequestParam Long memberId) {
        replyService.removeReply(replyId , memberId);

        return ResponseEntity.ok("댓글이 삭제되었습니다 ");
    }



}
