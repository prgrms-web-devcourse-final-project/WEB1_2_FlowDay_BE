package org.example.flowday.domain.course.vote.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.exception.CourseException;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.vote.dto.VoteReqDTO;
import org.example.flowday.domain.course.vote.dto.VoteResDTO;
import org.example.flowday.domain.course.vote.service.VoteService;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/votes")
public class VoteController {

    private final VoteService voteService;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    // 투표 생성
    @PostMapping
    public ResponseEntity<VoteResDTO> createVote(
            @RequestBody VoteReqDTO voteReqDTO,
            Authentication authentication
    ) {
        Long id = memberRepository.findIdByLoginId(authentication.getName()).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);
        Course course = courseRepository.findById(voteReqDTO.getCourseId()).orElseThrow(CourseException.NOT_FOUND::get);

        if (!id.equals(course.getMember().getId()) && !id.equals(course.getMember().getPartnerId())) {
            throw CourseException.FORBIDDEN.get();
        }

        return ResponseEntity.ok(voteService.saveVote(voteReqDTO));
    }

    // 투표 조회
    @GetMapping("/{voteId}")
    public ResponseEntity<VoteResDTO> getVote(@PathVariable Long voteId) {
        return ResponseEntity.ok(voteService.findVote(voteId));
    }

    // 투표 완료 후 코스 수정
    @PutMapping("/{voteId}/spots/{spotId}")
    public ResponseEntity<CourseResDTO> updateCourseByVote(@PathVariable Long voteId, @PathVariable Long spotId) {
        return ResponseEntity.ok(voteService.updateCourseByVote(voteId, spotId));
    }

}
