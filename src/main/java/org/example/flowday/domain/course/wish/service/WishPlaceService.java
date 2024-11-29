package org.example.flowday.domain.course.wish.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.wish.dto.WishPlaceReqDTO;
import org.example.flowday.domain.course.wish.dto.WishPlaceResDTO;
import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.example.flowday.domain.course.wish.exception.WishPlaceException;
import org.example.flowday.domain.course.wish.repository.WishPlaceRepository;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class WishPlaceService {

    private final MemberRepository memberRepository;
    private final SpotRepository spotRepository;
    private final WishPlaceRepository wishPlaceRepository;

    // 위시 플레이스 생성
    public void saveWishPlace(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);

        WishPlace wishPlace = WishPlace.builder()
                .member(member)
                .build();

        wishPlaceRepository.save(wishPlace);
    }

    // 위시 플레이스 장소 추가
    public void updateSpotInWishPlace(Long userId, WishPlaceReqDTO wishPlaceReqDTO) {
        if(!userId.equals(wishPlaceReqDTO.getMemberId())) {
            throw WishPlaceException.FORBIDDEN.get();
        }

        try {
            WishPlace wishPlace = wishPlaceRepository.findByMemberId(wishPlaceReqDTO.getMemberId()).orElseThrow(WishPlaceException.NOT_FOUND::get);

            Spot newSpot = Spot.builder()
                    .placeId(wishPlaceReqDTO.getSpot().getPlaceId())
                    .name(wishPlaceReqDTO.getSpot().getName())
                    .city(wishPlaceReqDTO.getSpot().getCity())
                    .comment(wishPlaceReqDTO.getSpot().getComment())
                    .wishPlace(wishPlace)
                    .build();

            spotRepository.save(newSpot);
        } catch (Exception e) {
            e.printStackTrace();
            throw WishPlaceException.NOT_UPDATED.get();
        }
    }

    // 위시 플레이스 장소 삭제
    public void removeSpotFromWishPlace(Long userId, Long memberId, Long spotId) {
        if(!userId.equals(memberId)) {
            throw WishPlaceException.FORBIDDEN.get();
        }

        try {
            WishPlace wishPlace = wishPlaceRepository.findByMemberId(memberId).orElseThrow(WishPlaceException.NOT_FOUND::get);

            Spot spotToRemove = wishPlace.getSpots().stream()
                    .filter(spot -> spot.getId().equals(spotId))
                    .findFirst()
                    .orElse(null);

            wishPlace.getSpots().remove(spotToRemove);
            spotRepository.delete(spotToRemove);
        } catch (Exception e) {
            e.printStackTrace();
            throw WishPlaceException.NOT_DELETED.get();
        }
    }

    // 회원 별 위시 플레이스 목록 조회
    public List<WishPlaceResDTO> getMemberWishPlaces(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);
        List<WishPlace> wishPlaces = wishPlaceRepository.findAllByMemberId(memberId);

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
    public List<WishPlaceResDTO> getMemberAndPartnerWishPlaces(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);
        Long partnerId = member.getPartnerId();

        List<WishPlace> wishPlaces = new ArrayList<>(wishPlaceRepository.findAllByMemberId(memberId));

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
