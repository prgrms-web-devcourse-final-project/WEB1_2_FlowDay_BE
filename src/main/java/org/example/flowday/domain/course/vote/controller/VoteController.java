package org.example.flowday.domain.course.vote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.vote.dto.VoteReqDTO;
import org.example.flowday.domain.course.vote.dto.VoteResDTO;
import org.example.flowday.domain.course.vote.service.VoteService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/votes")
@Tag(name = "Vote", description = "투표 관련 api")
public class VoteController {

    private final VoteService voteService;

    // 투표 생성
    @Operation(summary = "생성", description = "알림 도메인 완성 시 변경 예정")
    @PostMapping
    public ResponseEntity<VoteResDTO> createVote(
            @RequestBody VoteReqDTO voteReqDTO,
            @AuthenticationPrincipal SecurityUser member
    ) {
        return ResponseEntity.ok(voteService.saveVote(member.getId(), voteReqDTO));
    }

    // 투표 조회
    @Operation(summary = "조회")
    @GetMapping("/{voteId}")
    public ResponseEntity<VoteResDTO> getVote(@PathVariable Long voteId) {
        return ResponseEntity.ok(voteService.findVote(voteId));
    }

    // 투표 완료 후 코스 수정
    @Operation(summary = "투표 후 코스 수정", description = "파트너가 투표한 장소를 코스에 추가")
    @PutMapping("/{voteId}/spot/{spotId}")
    public ResponseEntity<CourseResDTO> updateCourseByVote(@PathVariable Long voteId, @PathVariable Long spotId) {
        return ResponseEntity.ok(voteService.updateCourseByVote(voteId, spotId));
    }

}
