package org.example.flowday.domain.course.course.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.dto.PageReqDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.exception.CourseException;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.exception.SpotException;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.spot.service.SpotService;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final SpotRepository spotRepository;
    private final SpotService spotService;
    private final WishPlaceService wishPlaceService;

    // 코스 생성
    public CourseResDTO saveCourse(SecurityUser user, CourseReqDTO courseReqDTO) {try {
            Course course = Course.builder()
                    .member(user.member())
                    .title(courseReqDTO.getTitle())
                    .status(courseReqDTO.getStatus())
                    .date(courseReqDTO.getDate())
                    .color(courseReqDTO.getColor())
                    .spots(null)
                    .build();

            courseRepository.save(course);

            CourseResDTO courseResDTO = new CourseResDTO(course, null);

            return courseResDTO;
        } catch (Exception e) {
            throw CourseException.NOT_CREATED.get();
        }
    }

    // 코스 조회
    public CourseResDTO findCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);
        List<Spot> spots = spotRepository.findAllByCourseIdOrderBySequenceAsc(courseId);

        List<SpotResDTO> spotResDTOs = new ArrayList<>();
        for (Spot spot : spots) {
            spotResDTOs.add(new SpotResDTO(spot));
        }

        return new CourseResDTO(course, spotResDTOs);
    }

    // 코스 수정 - 정보
    public CourseResDTO updateCourseInfo(Long userId, Long courseId, CourseReqDTO courseReqDTO) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);

        validateUserPermission(userId, course);

        try {
            if (courseReqDTO.getTitle() != null) {
                course.changeTitle(courseReqDTO.getTitle());
            }
            if (courseReqDTO.getDate() != null) {
                course.changeDate(courseReqDTO.getDate());
            }
            if (courseReqDTO.getColor() != null) {
                course.changeColor(courseReqDTO.getColor());
            }
            if (courseReqDTO.getStatus() != null) {
                course.changeStatus(courseReqDTO.getStatus());
            }

            List<SpotResDTO> spotResDTOs = spotRepository.findAllByCourseIdOrderBySequenceAsc(courseId)
                    .stream()
                    .map(SpotResDTO::new)
                    .collect(Collectors.toList());

            return new CourseResDTO(course, spotResDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            throw CourseException.NOT_UPDATED.get();
        }
    }

    // 코스 수정 - 장소 순서 변경
    public void updateCourseSpotSequence(Long userId, Long courseId, Long spotId, int sequence) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);

        validateUserPermission(userId, course);

        try {
            List<Spot> spots = spotRepository.findAllByCourseIdOrderBySequenceAsc(courseId);

            // spotId에 해당하는 Spot 찾기
            Spot inpustSpot = spots.stream()
                    .filter(spot -> spot.getId().equals(spotId))
                    .findFirst()
                    .orElseThrow(SpotException.NOT_FOUND::get);

            updateSpotSequence(spots, inpustSpot, sequence);

            spotRepository.saveAll(spots);

        } catch (Exception e) {
            e.printStackTrace();
            throw CourseException.NOT_UPDATED.get();
        }
    }

    // 코스에 장소 1개 추가
    public void addSpot(Long userId, Long courseId, SpotReqDTO spotReqDTO) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);

        validateUserPermission(userId, course);

        try {
            spotService.addSpot(userId, courseId, spotReqDTO, "course");
        } catch (Exception e) {
            e.printStackTrace();
            throw SpotException.NOT_CREATED.get();
        }
    }

    // 코스의 장소 1개 삭제
    public void removeSpot(Long userId, Long courseId, Long spotId) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);

        validateUserPermission(userId, course);

        try {
            spotService.removeSpot(userId, courseId, spotId, "course");
        } catch (Exception e) {
            e.printStackTrace();
            throw SpotException.NOT_DELETED.get();
        }
    }

    // 코스 삭제
    public CourseResDTO removeCourse(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);

        if(!userId.equals(courseRepository.findById(courseId).get().getMember().getId())) {
            throw CourseException.FORBIDDEN.get();
        }

        courseRepository.delete(course);

        return new CourseResDTO(course, null);
    }

    // 회원 별 코스 목록 조회
    public List<CourseResDTO> findCourseByMember(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);
        Long partnerId = member.getPartnerId() != null ? member.getPartnerId() : null;

        List<Course> memberCourses = courseRepository.findAllByMemberId(userId);
        List<Course> partnerCourses = partnerId != null
                ? courseRepository.findAllByMemberIdAndStatus(partnerId, Status.COUPLE)
                : new ArrayList<>();

        List<Course> combinedCourses = new ArrayList<>();
        combinedCourses.addAll(memberCourses);
        combinedCourses.addAll(partnerCourses);

        List<CourseResDTO> courseResDTOs = combinedCourses.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(course -> {
                    List<Spot> spots = spotRepository.findAllByCourseIdOrderBySequenceAsc(course.getId());
                    List<SpotResDTO> spotResDTOs = spots.stream()
                            .map(SpotResDTO::new)
                            .toList();

                    return new CourseResDTO(course, spotResDTOs);
                })
                .toList();

        return courseResDTOs;
    }

    // 회원 별 위시 플레이스, 코스 목록 조회
    public Page<Object> findWishPlaceAndCourseListByMember(SecurityUser user, PageReqDTO pageReqDTO) {
        Pageable pageable = pageReqDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<WishPlaceResDTO> wishPlaceResDTOS = wishPlaceService.getMemberAndPartnerWishPlaces(user);
        List<CourseResDTO> courseResDTOS = findCourseByMember(user.getId());

        List<Object> combinedCourses = new ArrayList<>();
        combinedCourses.addAll(wishPlaceResDTOS);
        combinedCourses.addAll(courseResDTOS);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), combinedCourses.size());
        List<Object> paginatedList = combinedCourses.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, combinedCourses.size());
    }

    // 그만 보기 시 상대방의 코스 비공개로 상태 변경
    public void updateCourseStatusToPrivate(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(CourseException.NOT_FOUND::get);

        if (!userId.equals(courseRepository.findById(courseId).get().getMember().getPartnerId())) {
            throw CourseException.FORBIDDEN.get();
        }

        try {
            course.changeStatus(Status.PRIVATE);
            courseRepository.save(course);
        } catch (Exception e) {
            throw CourseException.NOT_UPDATED.get();
        }
    }

    // 연인 관계 해지 시 모든 코스 비공개로 변경
    public void updateCourseListStatusToPrivate(Long userId) {
        List<Course> courses = courseRepository.findAllByMemberId(userId);

        for (Course course : courses) {
            if (Status.COUPLE.equals(course.getStatus())) {
                course.changeStatus(Status.PRIVATE);
            }
        }
    }

    // 사용자 비교
    private void validateUserPermission(Long userId, Course course) {
        if (!userId.equals(course.getMember().getId()) && !userId.equals(course.getMember().getPartnerId())) {
            throw CourseException.FORBIDDEN.get();
        }
    }

    // 순서 재배열
    private void updateSpotSequence(List<Spot> spots, Spot targetSpot, int newSequence) {
        int oldSequence = targetSpot.getSequence();
        targetSpot.changeSequence(newSequence);

        for (Spot spot : spots) {
            if (spot.getId().equals(targetSpot.getId())) continue;

            if (oldSequence < newSequence && spot.getSequence() > oldSequence && spot.getSequence() <= newSequence) {
                spot.changeSequence(spot.getSequence() - 1);
            } else if (oldSequence > newSequence && spot.getSequence() < oldSequence && spot.getSequence() >= newSequence) {
                spot.changeSequence(spot.getSequence() + 1);
            }
        }
    }

}
