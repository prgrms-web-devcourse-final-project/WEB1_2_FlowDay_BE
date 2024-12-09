package org.example.flowday.domain.course.wish.dto;

import lombok.Getter;
import org.example.flowday.domain.course.course.dto.PlaceIdDTO;
import org.example.flowday.domain.course.wish.entity.WishPlace;

import java.util.List;

@Getter
public class WishPlaceListResDTO {
    private Long id;
    private Long memberId;
    private List<PlaceIdDTO> spots;

    public WishPlaceListResDTO(WishPlace wishPlace,  List<PlaceIdDTO> spots) {
        this.id = wishPlace.getId();
        this.memberId = wishPlace.getMember().getId();
        this.spots = spots;
    }
}
