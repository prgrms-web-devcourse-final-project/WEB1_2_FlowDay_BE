package org.example.flowday.domain.course.spot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.service.CourseService;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.SecurityUser;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SpotController spotController;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private CourseReqDTO courseReqDTO;
    private CourseResDTO courseResDTO;
    private SpotReqDTO spotReqDTO1;
    private SpotReqDTO spotReqDTO2;
    private SpotReqDTO spotReqDTO3;
    private SpotReqDTO spotReqDTO4;

    @BeforeAll
    void setUp() {
        member = Member.builder()
                .name("tester20")
                .loginId("testId20")
                .pw("password")
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);
        SecurityUser securityUser = new SecurityUser(member);

        courseReqDTO = CourseReqDTO.builder()
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .build();

        courseResDTO = courseService.saveCourse(securityUser, courseReqDTO);

        spotReqDTO1 = SpotReqDTO.builder()
                .placeId("kasal")
                .name("장소1")
                .city("서울")
                .build();

    }

    @DisplayName("지역별 인기 장소 Top 4 조회 테스트")
    @Test
    @WithUserDetails(value = "testId20", userDetailsServiceBeanName = "securityUserService")
    void getTopSpotsByCity() throws Exception {
        mockMvc.perform(get("/api/v1/spots")
                        .param("city", "서울")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @DisplayName("지역별 인기 장소가 없는 경우 테스트")
    @Test
    @WithUserDetails(value = "testId20", userDetailsServiceBeanName = "securityUserService")
    void getTopSpotsByCity_noSpots() throws Exception {
        mockMvc.perform(get("/api/v1/spots")
                        .param("city", "부산")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
//
//    @DisplayName("commenet 수정 테스트")
//    @Test
//    @WithUserDetails(value = "testId20", userDetailsServiceBeanName = "securityUserService")
//    void updateComment() throws Exception {
//        SpotReqDTO spotReqDTO = SpotReqDTO.builder()
//                .comment("수정")
//                .build();
//
//        mockMvc.perform(patch("/api/v1/spots/{spotId}", courseResDTO.getSpots().get(0).getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(spotReqDTO)))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(jsonPath("$.comment").value("수정"));
//    }

}
