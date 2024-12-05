package org.example.flowday.domain.course.course.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.dto.PageReqDTO;
import org.example.flowday.domain.course.course.service.CourseService;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
@Tag(name = "Course", description = "코스 관련 api")
public class CourseController {

    private final CourseService courseService;

    // 코스 생성
    @Operation(summary = "생성")
    @PostMapping
    public ResponseEntity<CourseResDTO> createCourse(@RequestBody CourseReqDTO courseReqDTO, @AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(courseService.saveCourse(user, courseReqDTO));
    }

    // 코스 조회
    @Operation(summary = "조회", description = "코스 단일 조회")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResDTO> getCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.findCourse(courseId));
    }

    // 코스 수정 - 정보
    @Operation(summary = "코스 정보 수정", description = "수정된 값만 넘겨주면 됨")
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResDTO> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseReqDTO courseReqDTO,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(courseService.updateCourseInfo(user.getId(), courseId, courseReqDTO));
    }

    // 코스 수정 - 장소 순서 변경
    @Operation(summary = "장소 순서 변경")
    @PatchMapping("/{courseId}/spot/{spotId}/sequence/{sequence}")
    public ResponseEntity<Void> updateSpotSequence(
            @PathVariable Long courseId,
            @PathVariable Long spotId,
            @PathVariable int sequence,
            @AuthenticationPrincipal SecurityUser user
    ) {
        courseService.updateCourseSpotSequence(user.getId(), courseId, spotId, sequence);
        return ResponseEntity.noContent().build();
    }

    // 코스에 장소 1개 추가
    @Operation(summary = "장소 1개 추가")
    @PostMapping("/{courseId}")
    public ResponseEntity<Void> addSpotToCourse(
            @PathVariable Long courseId,
            @RequestBody SpotReqDTO spotReqDTO,
            @AuthenticationPrincipal SecurityUser user
    ) {
        courseService.addSpot(user.getId(), courseId, spotReqDTO);
        return ResponseEntity.noContent().build();
    }

    // 코스의 장소 1개 삭제
    @Operation(summary = "장소 1개 삭제")
    @DeleteMapping("/{course_id}/spot/{spot_id}")
    public ResponseEntity<Void> deleteSpotFromCourse(
            @PathVariable("course_id") Long courseId,
            @PathVariable("spot_id") Long spotId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        courseService.removeSpot(user.getId(), courseId, spotId);
        return ResponseEntity.noContent().build();
    }

    // 코스 삭제
    @Operation(summary = "삭제")
    @DeleteMapping("/{course_id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable("course_id") Long courseId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        courseService.removeCourse(user.getId(), courseId);
        return ResponseEntity.noContent().build();
    }

    // 회원 별 위시 플레이스, 코스 목록 조회
    @Operation(summary = "위시 플레이스 + 코스 목록 조회", description = "나와 (파트너의) 위시플레이스와 나와 (파트너의 COUPLE 상태) 코스 목록")
    @GetMapping
    public ResponseEntity<Page<Object>> CourseListByMember(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        PageReqDTO pageReqDTO = PageReqDTO.builder().page(page).size(size).build();
        return ResponseEntity.ok(courseService.findWishPlaceAndCourseListByMember(user, pageReqDTO));
    }

    // 그만 보기 시 상대방의 코스 비공개로 상태 변경
    @Operation(summary = "비공개로 상태 변경", description = "(그만 보기) 상대방의 코스를 PRIVATE으로 상태 변경")
    @PatchMapping("/{courseId}/private")
    public ResponseEntity<Void> updateCourseStatusToPrivate(
            @PathVariable Long courseId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        courseService.updateCourseStatusToPrivate(user.getId(), courseId);
        return ResponseEntity.ok().build();
    }

}
