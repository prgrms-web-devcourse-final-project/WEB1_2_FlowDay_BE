package org.example.flowday.domain.course.wish.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.member.entity.Member;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    private WishPlaceReqDTO wishPlaceReqDTO;
    private WishPlaceReqDTO wishPlaceReqDTO2;
    private WishPlaceResDTO wishPlaceResDTO;

    @BeforeAll
    void setUp() {
        member = Member.builder()
                .name("tester")
                .loginId("testId")
                .pw("password")
                .build();

        memberRepository.save(member);
        wishPlaceService.saveWishPlace(member.getId());

        partner = Member.builder()
                .name("tester2")
                .loginId("testId2")
                .pw("password2")
                .partnerId(member.getId())
                .build();

        memberRepository.save(partner);
        wishPlaceService.saveWishPlace(partner.getId());

        wishPlaceReqDTO = WishPlaceReqDTO.builder()
                .memberId(member.getId())
                .spot(SpotReqDTO.builder()
                        .placeId("ChIJgUbEo1")
                        .name("장소 이름1")
                        .city("서울")
                        .sequence(1)
                        .build())
                .build();

        wishPlaceResDTO = wishPlaceService.updateSpotInWishPlace(wishPlaceReqDTO);

        wishPlaceReqDTO2 = WishPlaceReqDTO.builder()
                .memberId(partner.getId())
                .spot(SpotReqDTO.builder()
                        .placeId("ChIJgUbEo2")
                        .name("장소 이름2")
                        .city("대전")
                        .sequence(1)
                        .build())
                .build();

        wishPlaceService.updateSpotInWishPlace(wishPlaceReqDTO2);

    }

    @DisplayName("위시 플레이스 장소 추가 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void addSpotToWishPlace() throws Exception {
        mockMvc.perform(post("/api/v1/wishPlaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishPlaceReqDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.memberId").value(member.getId()));
    }

    @DisplayName("위시 플레이스 장소 삭제 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void removeSpotFromWishPlace() throws Exception {
        mockMvc.perform(delete("/api/v1/wishPlaces/member/{memberId}/spot/{spotId}", member.getId(), wishPlaceResDTO.getSpots().get(0).getId()))
                .andExpect(status().isNoContent());
    }

    @DisplayName("회원 별 위시 플레이스 목록 조회 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void getMemberAndPartnerWishPlaces() throws Exception {
        mockMvc.perform(get("/api/v1/wishPlaces/member/{memberId}", partner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].memberId").value(partner.getId()))
                .andExpect(jsonPath("$[1].memberId").value(partner.getPartnerId()));
    }

}
