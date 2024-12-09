package org.example.flowday.domain.course.wish.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WishPlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WishPlaceService wishPlaceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Member partner;
    private SpotReqDTO spotReqDTO;
    private SpotReqDTO spotReqDTO2;
    private WishPlaceResDTO wishPlaceResDTO;

    @BeforeAll
    void setUp() {
        member = Member.builder()
                .name("tester4")
                .loginId("testId4")
                .pw("password")
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);
        wishPlaceService.saveWishPlace(member.getId());

        partner = Member.builder()
                .name("tester5")
                .loginId("testId5")
                .pw("password2")
                .partnerId(member.getId())
                .build();

        memberRepository.save(partner);
        wishPlaceService.saveWishPlace(partner.getId());

        spotReqDTO= SpotReqDTO.builder()
                .id(1L)
                .placeId("ChIJgUbEo3")
                .name("성심당")
                .city("대전")
                .build();

        wishPlaceService.updateSpotInWishPlace(member.getId(), spotReqDTO);

        spotReqDTO2 = SpotReqDTO.builder()
                .id(2L)
                .placeId("ChIJgUbEo5")
                .name("바다")
                .city("울산")
                .build();

        wishPlaceService.updateSpotInWishPlace(partner.getId(), spotReqDTO2);

    }

    @DisplayName("위시 플레이스 장소 추가 테스트")
    @Test
    @WithUserDetails(value = "testId4", userDetailsServiceBeanName = "securityUserService")
    void addSpotToWishPlace() throws Exception {
        mockMvc.perform(post("/api/v1/wishPlaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spotReqDTO)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("위시 플레이스 장소 삭제 테스트")
    @Test
    @WithUserDetails(value = "testId4", userDetailsServiceBeanName = "securityUserService")
    void removeSpotFromWishPlace() throws Exception {
        mockMvc.perform(delete("/api/v1/wishPlaces/spot/{spotId}", wishPlaceService.findMemberWishPlaces(member.getId()).get(0).getSpots().get(0).getId()))
                .andExpect(status().isNoContent());
    }

    @DisplayName("회원 별 위시 플레이스 목록 조회 테스트")
    @Test
    @WithUserDetails(value = "testId4", userDetailsServiceBeanName = "securityUserService")
    void getMemberAndPartnerWishPlaces() throws Exception {
        mockMvc.perform(get("/api/v1/wishPlaces", member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

}
