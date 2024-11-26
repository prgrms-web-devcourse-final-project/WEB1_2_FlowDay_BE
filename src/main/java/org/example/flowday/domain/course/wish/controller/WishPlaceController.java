package org.example.flowday.domain.course.wish.controller;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.wish.dto.WishPlaceReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.exception.WishPlaceException;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.example.flowday.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wishPlaces")
public class WishPlaceController {

    private final WishPlaceService wishPlaceService;

    // 위시 플레이스에 장소 추가
    @PostMapping
    public ResponseEntity<WishPlaceResDTO> addSpotToWishPlace(
            @RequestBody WishPlaceReqDTO wishPlaceReqDTO,
            @AuthenticationPrincipal Member user
    ) {
        if(!user.getId().equals(wishPlaceReqDTO.getMemberId())) {
            throw WishPlaceException.FORBIDDEN.get();
        }

        WishPlaceResDTO updatedWishPlace = wishPlaceService.updateSpotInWishPlace(wishPlaceReqDTO);
        return ResponseEntity.ok(updatedWishPlace);
    }

    // 위시 플레이스에서 장소 삭제
    @DeleteMapping("/member/{memberId}/spot/{spotId}")
    public ResponseEntity<Void> removeSpotFromWishPlace(
            @PathVariable Long memberId,
            @PathVariable Long spotId,
            @AuthenticationPrincipal Member user
    ) {
        if(!user.getId().equals(memberId)) {
            throw WishPlaceException.FORBIDDEN.get();
        }

        wishPlaceService.removeSpotFromWishPlace(memberId, spotId);
        return ResponseEntity.noContent().build();
    }

    // 회원 및 파트너의 위시 플레이스 목록 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<WishPlaceResDTO>> getMemberAndPartnerWishPlaces(@PathVariable Long memberId) {
        List<WishPlaceResDTO> wishPlaces = wishPlaceService.getMemberAndPartnerWishPlaces(memberId);
        return ResponseEntity.ok(wishPlaces);
    }

}
