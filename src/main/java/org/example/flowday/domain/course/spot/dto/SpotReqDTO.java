package org.example.flowday.domain.course.spot.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SpotReqDTO {
    private Long id;
    private String placeId;
    private String name;
    private String city;
    private String comment;
    private int sequence;
}
