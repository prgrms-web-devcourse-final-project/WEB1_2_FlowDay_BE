package org.example.flowday.domain.course.course.service;

import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.dto.PageReqDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private WishPlaceService wishPlaceService;

    @InjectMocks
    private CourseService courseService;

    private Member member;
    private WishPlace wishPlace;
    private Member partner;
    private WishPlace wishPlace2;
    private Spot spot;
    private Spot addSpot;
    private Spot addSpot2;
    private Course course;
    private Course course2;
    private Course course3;
    private Course course4;

    @BeforeEach
    public void CourseServiceTest() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .id(1L)
                .loginId("testId")
                .pw("testPw")
                .email("test@test.com")
                .name("tester")
                .refreshToken("refresh_token_value")
                .role(Role.ROLE_USER)
                .partnerId(2L)
                .build();

        wishPlace = WishPlace.builder()
                .id(1L)
                .member(member)
                .spots(new ArrayList<>())
                .build();

        partner = Member.builder()
                .id(2L)
                .loginId("testId2")
                .pw("testPw")
                .email("test2@test.com")
                .name("tester2")
                .refreshToken("refresh_token_value")
                .role(Role.ROLE_USER)
                .partnerId(1L)
                .build();

        wishPlace2 = WishPlace.builder()
                .id(2L)
                .member(partner)
                .spots(new ArrayList<>())
                .build();

        spot = Spot.builder()
                .id(1L)
                .placeId("ChIJgUbEo1")
                .name("장소 이름1")
                .city("서울")
                .sequence(1)
                .build();

        addSpot = Spot.builder()
                .id(2L)
                .placeId("ChIJgUbEo1")
                .name("성심당")
                .city("서울")
                .sequence(2)
                .build();

        addSpot2 = Spot.builder()
                .id(3L)
                .placeId("ChIJgUbEo1")
                .name("성심당")
                .city("서울")
                .sequence(3)
                .build();

        course = Course.builder()
                .id(1L)
                .member(member)
                .title("코스 이름")
                .status(Status.PRIVATE)
                .date(LocalDate.now())
                .color("#FFFFFF")
                .spots(List.of(spot))
                .createdAt(LocalDateTime.now())
                .build();

        course2 = Course.builder()
                .id(2L)
                .member(member)
                .title("코스 이름2")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("#FFFFFF")
                .spots(List.of(spot))
                .createdAt(LocalDateTime.now())
                .build();

        course3 = Course.builder()
                .id(3L)
                .member(partner)
                .title("코스 이름")
                .status(Status.PRIVATE)
                .date(LocalDate.now())
                .color("#FFFFFF")
                .spots(List.of(spot))
                .createdAt(LocalDateTime.now())
                .build();

        course4 = Course.builder()
                .id(4L)
                .member(partner)
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("#FFFFFF")
                .createdAt(LocalDateTime.now())
                .spots(List.of(spot))
                .build();
    }

    @DisplayName("코스 생성 테스트")
    @Test
    void saveCourse() {
        CourseReqDTO courseReqDTO = CourseReqDTO.builder()
                .memberId(1L)
                .title("코스 이름")
                .status(Status.PRIVATE)
                .date(LocalDate.now())
                .color("#FFFFFF")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        CourseResDTO result = courseService.saveCourse(courseReqDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("코스 이름");
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @DisplayName("코스 조회 테스트")
    @Test
    void findCourse() {
        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(spotRepository.findAllByCourseIdOrderBySequenceAsc(courseId)).thenReturn(List.of(spot));

        CourseResDTO result = courseService.findCourse(courseId);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("코스 이름");
        assertThat(result.getSpots()).hasSize(1);
        assertThat(result.getSpots().get(0).getName()).isEqualTo("장소 이름1");
    }

    @DisplayName("코스 수정 - 정보 테스트")
    @Test
    void updateCourse() {
        Long memberId = 1L;
        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        CourseReqDTO courseReqDTO = CourseReqDTO.builder()
                .memberId(memberId)
                .title("수정 코스 이름")
                .status(Status.PRIVATE)
                .date(LocalDate.now())
                .color("#FFFFFF")
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(spotRepository.findAllByCourseIdOrderBySequenceAsc(courseId)).thenReturn(List.of(spot));

        CourseResDTO result = courseService.updateCourseInfo(member.getId(), courseId, courseReqDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("수정 코스 이름");
    }

    @DisplayName("코스 장소 순서 변경 테스트")
    @Test
    void updateCourseSpotSequence() {
        Long memberId = 1L;
        Long courseId = 1L;
        Long spotId = 1L;
        int newSequence = 2;

        List<Spot> spots = Arrays.asList(spot, addSpot);

        SpotReqDTO spotReqDTO1 = SpotReqDTO.builder()
                .placeId("ChIJgUbEo1")
                .name("장소 이름1")
                .city("서울")
                .build();

        SpotReqDTO spotReqDTO2 = SpotReqDTO.builder()
                .placeId("ChIJgUbEo1")
                .name("성심당")
                .city("서울")
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(spotRepository.findAllByCourseIdOrderBySequenceAsc(courseId)).thenReturn(spots);

        courseService.addSpot(memberId, courseId, spotReqDTO1);
        courseService.addSpot(memberId, courseId, spotReqDTO2);

        courseService.updateCourseSpotSequence(memberId, courseId, spotId, newSequence);

        assertEquals(2, spot.getSequence());

        verify(spotRepository).saveAll(spots);
    }

    @DisplayName("코스에 장소 1개 추가 테스트")
    @Test
    void addSpotToCourse() {
        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        SpotReqDTO spotReqDTO = SpotReqDTO.builder()
                .placeId("ChIJgUbEo1")
                .name("성심당")
                .city("서울")
                .build();

        courseService.addSpot(member.getId(), courseId, spotReqDTO);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(spotRepository.findAllByCourseIdOrderBySequenceAsc(courseId)).thenReturn(List.of(addSpot));

        verify(spotRepository, times(1)).save(any(Spot.class));
        assertThat(course.getSpots().get(0).getSequence()).isEqualTo(1);
    }

    @DisplayName("코스 삭제 테스트")
    @Test
    void removeCourse() {
        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(spotRepository.findAllByCourseIdOrderBySequenceAsc(courseId)).thenReturn(List.of(spot));


        CourseResDTO result = courseService.removeCourse(member.getId(), courseId);

        assertThat(result).isNotNull();
        verify(courseRepository, times(1)).delete(course);
    }

    @DisplayName("회원 별 코스 목록 조회 테스트")
    @Test
    void findCourseByMember() {
        Long memberId = 2L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(partner));
        when(courseRepository.findAllByMemberId(memberId)).thenReturn(List.of(course2, course3, course4));
        when(courseRepository.findAllByMemberId(member.getId())).thenReturn(List.of(course3, course4));

        List<CourseResDTO> result = courseService.findCourseByMember(memberId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getTitle()).isEqualTo(course3.getTitle());
        assertThat(result.get(1).getTitle()).isEqualTo(course4.getTitle());

        verify(memberRepository, times(1)).findById(memberId);
        verify(courseRepository, times(1)).findAllByMemberId(memberId);
    }

    @DisplayName("회원 별 위시 플레이스, 코스 목록 조회 테스트")
    @Test
    void findWishPlaceAndCourseListByMember() {
        Long memberId = 1L;

        PageReqDTO pageReqDTO = PageReqDTO.builder()
                .page(1)
                .size(10)
                .build();

        List<WishPlaceResDTO> wishPlaceResDTOList = List.of(
                new WishPlaceResDTO(wishPlace, List.of(new SpotResDTO(spot))),
                new WishPlaceResDTO(wishPlace2, List.of(new SpotResDTO(spot)))
        );

        List<CourseResDTO> courseResDTOList = List.of(
                new CourseResDTO(course, List.of(new SpotResDTO(spot))),
                new CourseResDTO(course2, List.of(new SpotResDTO(spot)))
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(wishPlaceService.getMemberAndPartnerWishPlaces(memberId)).thenReturn(wishPlaceResDTOList);
        when(courseService.findCourseByMember(memberId)).thenReturn(courseResDTOList);

        List<Object> combinedList = new ArrayList<>();
        combinedList.addAll(wishPlaceResDTOList);
        combinedList.addAll(courseResDTOList);

        int start = (int) pageReqDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt")).getOffset();
        int end = Math.min((start + pageReqDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt")).getPageSize()), combinedList.size());
        List<Object> paginatedList = combinedList.subList(start, end);

        Page<Object> result = new PageImpl<>(paginatedList, pageReqDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt")), combinedList.size());

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isGreaterThan(0);

        List<Object> content = result.getContent();
        assertThat(content).hasSize(4);
    }

    @DisplayName("그만 보기 시 상대방의 코스를 비공개로 상태 변경")
    @Test
    void updateCourseStatusToPrivate() {
        Long courseId = 3L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course3));

        courseService.updateCourseStatusToPrivate(member.getId(), courseId);

        assertThat(course3.getStatus()).isEqualTo(Status.PRIVATE);

        verify(courseRepository, times(1)).save(course3);
    }

    @DisplayName("연인 관계 해지 시 모든 코스 비공개로 변경 테스트")
    @Test
    void updateCourseListStatusToPrivate() {
        Long memberId = 2L;

        courseService.updateCourseListStatusToPrivate(memberId);

        List<Course> courses =courseRepository.findAllByMemberId(memberId);
        courses.forEach(c -> assertThat(c.getStatus()).isEqualTo(Status.PRIVATE));
    }

}
