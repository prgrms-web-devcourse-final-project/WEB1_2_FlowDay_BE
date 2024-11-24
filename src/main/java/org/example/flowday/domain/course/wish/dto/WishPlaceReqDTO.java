package org.example.flowday.domain.course.wish.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;

@Builder
@Getter
public class WishPlaceReqDTO {
    private Long memberId;
    private SpotReqDTO spot;
}
