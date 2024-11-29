package org.example.flowday.domain.course.wish.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.wish.dto.WishPlaceReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.service.WishPlaceService;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Void> addSpotToWishPlace(
            @RequestBody WishPlaceReqDTO wishPlaceReqDTO,
            @AuthenticationPrincipal SecurityUser user
    ) {
        wishPlaceService.updateSpotInWishPlace(user.getId(), wishPlaceReqDTO);
        return ResponseEntity.noContent().build();
    }

    // 위시 플레이스에서 장소 삭제
    @Operation(summary = "장소 삭제")
    @DeleteMapping("/member/{memberId}/spot/{spotId}")
    public ResponseEntity<Void> removeSpotFromWishPlace(
            @PathVariable Long memberId,
            @PathVariable Long spotId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        wishPlaceService.removeSpotFromWishPlace(user.getId(), memberId, spotId);
        return ResponseEntity.noContent().build();
    }

    // 회원 별 위시 플레이스 목록 조회
    @Operation(summary = "회원 별 위시 플레이스 목록 조회")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<WishPlaceResDTO>> getMemberWishPlaces(@PathVariable Long memberId) {
        List<WishPlaceResDTO> wishPlaces = wishPlaceService.getMemberWishPlaces(memberId);
        return ResponseEntity.ok(wishPlaces);
    }

}
