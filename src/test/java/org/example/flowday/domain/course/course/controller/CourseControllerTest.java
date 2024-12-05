package org.example.flowday.domain.course.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.dto.PageReqDTO;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.service.CourseService;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Member partner;
    private CourseReqDTO courseReqDTO;
    private CourseResDTO courseResDTO;
    private CourseReqDTO courseReqDTO2;
    private CourseResDTO courseResDTO2;

    @BeforeAll
    void setUp() {
        member = Member.builder()
                .name("tester")
                .loginId("testId")
                .role(Role.ROLE_USER)
                .pw("password")
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);

        partner = Member.builder()
                .name("partner")
                .loginId("testId2")
                .pw("password")
                .role(Role.ROLE_USER)
                .partnerId(member.getId())
                .build();

        memberRepository.save(partner);

        courseReqDTO = CourseReqDTO.builder()
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .build();

        courseResDTO = courseService.saveCourse(member.getId(), courseReqDTO);

        SpotReqDTO spotReqDTO1 = SpotReqDTO.builder()
                .id(1L)
                .placeId("ChIJgUbEo3")
                .name("성심당")
                .city("대전")
                .build();

        courseService.addSpot(member.getId(), courseResDTO.getId(), spotReqDTO1);
        courseResDTO = courseService.findCourse(courseResDTO.getId());

        SpotReqDTO spotReqDTO2 = SpotReqDTO.builder()
                .id(2L)
                .placeId("ChIJgUbEo5")
                .name("바다")
                .city("울산")
                .build();

        courseService.addSpot(member.getId(), courseResDTO.getId(), spotReqDTO2);
        courseResDTO = courseService.findCourse(courseResDTO.getId());

        courseReqDTO2 = CourseReqDTO.builder()
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .build();

        courseResDTO2 = courseService.saveCourse(partner.getId(), courseReqDTO2);
    }

    @DisplayName("코스 생성 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void createCourse() throws Exception {
        courseReqDTO = CourseReqDTO.builder()
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .build();

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReqDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("코스 이름"))
                .andExpect(jsonPath("$.status").value("COUPLE"))
                .andExpect(jsonPath("$.color").value("blue"));
    }

    @DisplayName("코스 조회 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void getCourse() throws Exception {
        mockMvc.perform(get("/api/v1/courses/{courseId}", courseResDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("코스 이름"))
                .andExpect(jsonPath("$.status").value("COUPLE"))
                .andExpect(jsonPath("$.color").value("blue"));
    }

    @DisplayName("코스 수정 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void updateCourse() throws Exception {
        courseReqDTO = CourseReqDTO.builder()
                .title("코스 이름 수정")
                .status(Status.PRIVATE)
                .date(LocalDate.now())
                .color("pink")
                .build();

        mockMvc.perform(put("/api/v1/courses/{courseId}", courseResDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReqDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value("코스 이름 수정"))
                .andExpect(jsonPath("$.status").value("PRIVATE"))
                .andExpect(jsonPath("$.color").value("pink"));
    }

    @DisplayName("장소 순서 변경 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void updateSpotSequence() throws Exception {
        System.out.println("xxx");
        System.out.println(courseResDTO.getSpots().size());
        Long spotId = courseResDTO.getSpots().get(0).getId();
        int newSequence = 2;

        mockMvc.perform(patch("/api/v1/courses/{courseId}/spot/{spotId}/sequence/{sequence}",
                        courseResDTO.getId(), spotId, newSequence)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("코스에 장소 1개 추가 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void addSpotToCourse() throws Exception {
        SpotReqDTO spotReqDTO = SpotReqDTO.builder()
                .placeId("ChIJgUbEo3")
                .name("성심당")
                .city("대전")
                .build();

        mockMvc.perform(post("/api/v1/courses/{courseId}", courseResDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spotReqDTO)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("코스의 장소 1개 삭제 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void deleteSpotFromCourse() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/{courseId}/spot/{spotId}", courseResDTO.getId(), courseResDTO.getSpots().get(0).getId()))
                .andExpect(status().isNoContent());
    }

    @DisplayName("코스 삭제 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void deleteCourse() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/{courseId}", courseResDTO.getId()))
                .andExpect(status().isNoContent());
    }

    @DisplayName("회원 별 위시 플레이스 및 코스 목록 조회 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void getCourseListByMember() throws Exception {
        PageReqDTO pageReqDTO = PageReqDTO.builder().page(1).size(10).build();

        mockMvc.perform(get("/api/v1/courses/member/{memberId}", member.getId())
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @DisplayName("비공개로 상태 변경 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void updateCourseStatusToPrivate() throws Exception {
        System.out.println("zzz");
        System.out.println(member.getId());
        System.out.println(member.getPartnerId());
        System.out.println(partner.getId());
        System.out.println(courseResDTO2.getMemberId());
        mockMvc.perform(patch("/api/v1/courses/{courseId}/private", courseResDTO2.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
