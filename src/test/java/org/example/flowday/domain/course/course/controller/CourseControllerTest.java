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
import java.util.Collections;
import java.util.List;

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
    private CourseReqDTO courseReqDTO;
    private CourseResDTO courseResDTO;

    @BeforeAll
    void setUp() {
        member = Member.builder()
                .name("tester")
                .loginId("testId")
                .role(Role.ROLE_USER)
                .pw("password")
                .build();

        memberRepository.save(member);

        courseReqDTO = CourseReqDTO.builder()
                .memberId(member.getId())
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .spots(Collections.singletonList(
                        SpotReqDTO.builder()
                                .id(1L)
                                .placeId("ChIJgUbEo1")
                                .name("장소 이름1")
                                .city("서울")
                                .sequence(1)
                                .build()
                ))
                .build();

        courseResDTO = courseService.saveCourse(courseReqDTO);
    }

    @DisplayName("코스 생성 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void createCourse() throws Exception {
        courseReqDTO = CourseReqDTO.builder()
                .memberId(member.getId())
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .spots(Collections.singletonList(
                        SpotReqDTO.builder()
                                .id(1L)
                                .placeId("ChIJgUbEo1")
                                .name("장소 이름1")
                                .city("서울")
                                .sequence(1)
                                .build()
                ))
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
                .memberId(member.getId())
                .title("코스 이름 수정")
                .status(Status.PRIVATE)
                .date(LocalDate.now())
                .color("pink")
                .spots(List.of(
                        SpotReqDTO.builder()
                                .id(1L)
                                .placeId("ChIJgUbEo1")
                                .name("장소 이름 수정")
                                .city("서울")
                                .sequence(1)
                                .build(),
                        SpotReqDTO.builder()
                                .placeId("dkjdkfsj2")
                                .name("장소 이름2")
                                .city("대전")
                                .sequence(2)
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/courses/{courseId}", courseResDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReqDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value("코스 이름 수정"))
                .andExpect(jsonPath("$.status").value("PRIVATE"))
                .andExpect(jsonPath("$.color").value("pink"))
                .andExpect(jsonPath("$.spots[0].name").value("장소 이름 수정"))
                .andExpect(jsonPath("$.spots[1].name").value("장소 이름2"));
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
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value("코스 이름"))
                .andExpect(jsonPath("$.spots[1].name").value("성심당"))
                .andExpect(jsonPath("$.spots[1].sequence").value(2));
    }

    @DisplayName("코스 삭제 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void deleteCourse() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/{courseId}", courseResDTO.getId()))
                .andExpect(status().isNoContent());
    }

    @DisplayName("회원 별 위시 플레이스 및 코스 목록 조회 테스트 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void getCourseListByMember() throws Exception {
        PageReqDTO pageReqDTO = PageReqDTO.builder().page(1).size(10).build();

        mockMvc.perform(get("/api/v1/courses/member/{memberId}", member.getId())
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

}
