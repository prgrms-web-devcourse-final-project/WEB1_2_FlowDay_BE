package org.example.flowday.domain.course.wish.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.spot.service.SpotService;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.example.flowday.domain.course.wish.exception.WishPlaceException;
import org.example.flowday.domain.course.wish.repository.WishPlaceRepository;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WishPlaceService {

    private final MemberRepository memberRepository;
    private final SpotRepository spotRepository;
    private final WishPlaceRepository wishPlaceRepository;
    private final SpotService spotService;

    // 위시 플레이스 생성
    public void saveWishPlace(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);

        WishPlace wishPlace = WishPlace.builder()
                .member(member)
                .build();

        wishPlaceRepository.save(wishPlace);
    }

    // 위시 플레이스 장소 추가
    public void updateSpotInWishPlace(Long userId, SpotReqDTO spotReqDTO) {
        try {
            spotService.addSpot(userId, null, spotReqDTO, "wishPlace");
        } catch (Exception e) {
            e.printStackTrace();
            throw WishPlaceException.NOT_UPDATED.get();
        }
    }

    // 위시 플레이스 장소 삭제
    public void removeSpotFromWishPlace(Long userId, Long spotId) {
        try {
            spotService.removeSpot(userId, null, spotId, "wishPlace");
        } catch (Exception e) {
            e.printStackTrace();
            throw WishPlaceException.NOT_DELETED.get();
        }
    }

    // 회원 별 위시 플레이스 목록 조회
    public List<WishPlaceResDTO> getMemberWishPlaces(Long userId) {
        List<WishPlace> wishPlaces = wishPlaceRepository.findAllByMemberId(userId);

        return wishPlaces.stream()
                .map(wishPlace -> {
                    List<SpotResDTO> spotResDTOs = spotRepository.findAllByWishPlaceIdOrderByIdDesc(wishPlace.getId()).stream()
                            .map(SpotResDTO::new)
                            .collect(Collectors.toList());

                    return new WishPlaceResDTO(wishPlace, spotResDTOs);
                })
                .collect(Collectors.toList());
    }

    // 회원 별 위시 플레이스 목록 조회 + 파트너
    public List<WishPlaceResDTO> getMemberAndPartnerWishPlaces(SecurityUser user) {
        Long partnerId = user.member().getPartnerId();

        List<WishPlace> wishPlaces = new ArrayList<>(wishPlaceRepository.findAllByMemberId(user.getId()));

        if (partnerId != null) {
            wishPlaces.addAll(wishPlaceRepository.findAllByMemberId(partnerId));
        }

        return wishPlaces.stream()
                .map(wishPlace -> {
                    List<SpotResDTO> spotResDTOs = spotRepository.findAllByWishPlaceIdOrderByIdDesc(wishPlace.getId()).stream()
                            .map(SpotResDTO::new)
                            .collect(Collectors.toList());

                    return new WishPlaceResDTO(wishPlace, spotResDTOs);
                })
                .collect(Collectors.toList());
    }

}
