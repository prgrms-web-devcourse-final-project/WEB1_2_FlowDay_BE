package org.example.flowday.domain.course.wish.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.PlaceIdDTO;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.spot.service.SpotService;
import org.example.flowday.domain.course.wish.dto.WishPlaceListResDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishPlaceService {

    private final MemberRepository memberRepository;
    private final SpotRepository spotRepository;
    private final WishPlaceRepository wishPlaceRepository;
    private final SpotService spotService;

    // 위시 플레이스 생성
    @Transactional
    public void saveWishPlace(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);

        WishPlace wishPlace = WishPlace.builder()
                .member(member)
                .build();

        wishPlaceRepository.save(wishPlace);
    }

    // 위시 플레이스 장소 추가
    @Transactional
    public void updateSpotInWishPlace(Long userId, SpotReqDTO spotReqDTO) {
        try {
            spotService.addSpot(userId, null, spotReqDTO, "wishPlace");
        } catch (Exception e) {
            e.printStackTrace();
            throw WishPlaceException.NOT_UPDATED.get();
        }
    }

    // 위시 플레이스 장소 삭제
    @Transactional
    public void removeSpotFromWishPlace(Long userId, Long spotId) {
        try {
            spotService.removeSpot(userId, null, spotId, "wishPlace");
        } catch (Exception e) {
            e.printStackTrace();
            throw WishPlaceException.NOT_DELETED.get();
        }
    }

    // 나의 위시 플레이스 조회
    public List<WishPlaceResDTO> findMemberWishPlaces(Long userId) {
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

    // 파트너의 위시 플레이스 조회
    public List<WishPlaceResDTO> findPartnerWishPlaces(SecurityUser user) {
        List<WishPlace> wishPlaces = wishPlaceRepository.findAllByMemberId(user.member().getPartnerId());

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
    public List<WishPlaceListResDTO> findMemberAndPartnerWishPlaces(SecurityUser user) {
        Long partnerId = user.member().getPartnerId();
        List<Long> memberIds = new ArrayList<>();
        memberIds.add(user.getId());
        if (partnerId != null) {
            memberIds.add(partnerId);
        }

        List<Object[]> wishPlacesResults = wishPlaceRepository.findAllWithSpotsByMemberIds(memberIds);

        return wishPlacesResults.stream()
                .map(result -> {
                    WishPlace wishPlace = (WishPlace) result[0];
                    String placeIds = (String) result[1];

                    List<String> spotPlaceIds = (placeIds != null && !placeIds.isEmpty())
                            ? Arrays.asList(placeIds.split(","))
                            : new ArrayList<>();

                    List<PlaceIdDTO> spots = new ArrayList<>();
                    for (String placeId : spotPlaceIds) {
                        spots.add(new PlaceIdDTO(placeId, 0));
                    }

                    return new WishPlaceListResDTO(wishPlace, spots);
                })
                .collect(Collectors.toList());
    }

}
