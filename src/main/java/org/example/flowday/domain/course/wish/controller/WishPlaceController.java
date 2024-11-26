package org.example.flowday.domain.course.wish.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.wish.dto.WishPlaceReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.exception.WishPlaceException;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wishPlaces")
@Tag(name = "WishPlace", description = "위시 플레이스 관련 api")
public class WishPlaceController {

    private final WishPlaceService wishPlaceService;
    private final MemberRepository memberRepository;

    // 위시 플레이스에 장소 추가
    @Operation(summary = "장소 추가")
    @PostMapping
    public ResponseEntity<WishPlaceResDTO> addSpotToWishPlace(
            @RequestBody WishPlaceReqDTO wishPlaceReqDTO,
            Authentication authentication
    ) {
        Long id = memberRepository.findIdByLoginId(authentication.getName()).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);

        if(!id.equals(wishPlaceReqDTO.getMemberId())) {
            throw WishPlaceException.FORBIDDEN.get();
        }

        WishPlaceResDTO updatedWishPlace = wishPlaceService.updateSpotInWishPlace(wishPlaceReqDTO);
        return ResponseEntity.ok(updatedWishPlace);
    }

    // 위시 플레이스에서 장소 삭제
    @Operation(summary = "장소 삭제")
    @DeleteMapping("/member/{memberId}/spot/{spotId}")
    public ResponseEntity<Void> removeSpotFromWishPlace(
            @PathVariable Long memberId,
            @PathVariable Long spotId,
            Authentication authentication
    ) {
        Long id = memberRepository.findIdByLoginId(authentication.getName()).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);

        if(!id.equals(memberId)) {
            throw WishPlaceException.FORBIDDEN.get();
        }

        wishPlaceService.removeSpotFromWishPlace(memberId, spotId);
        return ResponseEntity.noContent().build();
    }

    // 회원 및 파트너의 위시 플레이스 목록 조회
    @Operation(summary = "불필요 api - 제거 예정")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<WishPlaceResDTO>> getMemberAndPartnerWishPlaces(@PathVariable Long memberId) {
        List<WishPlaceResDTO> wishPlaces = wishPlaceService.getMemberAndPartnerWishPlaces(memberId);
        return ResponseEntity.ok(wishPlaces);
    }

}
