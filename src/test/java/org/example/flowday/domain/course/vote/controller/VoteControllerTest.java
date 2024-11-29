package org.example.flowday.domain.course.vote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.vote.dto.VoteReqDTO;
import org.example.flowday.domain.course.vote.dto.VoteResDTO;
import org.example.flowday.domain.course.vote.service.VoteService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Course course;
    private Spot spot1;
    private Spot spot2;
    private VoteReqDTO voteReqDTO;
    private VoteResDTO voteResDTO;

    @BeforeAll
    void setUp() {
        member = Member.builder()
                .name("tester")
                .loginId("testId")
                .pw("password")
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);

        course = Course.builder()
                .member(member)
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .spots(Collections.singletonList(
                        Spot.builder()
                                .placeId("ChIJgUbEo1")
                                .name("장소 이름1")
                                .city("서울")
                                .sequence(1)
                                .build()
                ))
                .build();

        courseRepository.save(course);

        spot1 = Spot.builder()
                .id(1L)
                .placeId("ChIJgUbEo1")
                .name("장소 이름1")
                .city("서울")
                .build();

        spot2 = Spot.builder()
                .id(2L)
                .placeId("ChIJgUbEo1")
                .name("장소 이름2")
                .city("대전")
                .build();

        voteReqDTO = VoteReqDTO.builder()
                .courseId(course.getId())
                .title("뭐먹지")
                .spotIds(List.of(spot1.getId(), spot2.getId()))
                .build();

        voteResDTO = voteService.saveVote(member.getId(), voteReqDTO);

    }

    @DisplayName("투표 생성 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void createVote() throws Exception {
        mockMvc.perform(post("/api/v1/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteReqDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("뭐먹지"));
    }

    @DisplayName("투표 조회 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void getVote() throws Exception {
        mockMvc.perform(get("/api/v1/votes/{voteId}", voteResDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("뭐먹지"));
    }

    @DisplayName("투표 완료 후 코스 수정 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void updateCourseByVote() throws Exception {
        mockMvc.perform(patch("/api/v1/votes/{voteId}/spot/{spotId}", voteResDTO.getId(), spot1.getId()))
                .andExpect(status().isOk());
    }

}
