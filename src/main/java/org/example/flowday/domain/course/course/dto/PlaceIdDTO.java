package org.example.flowday.domain.course.course.dto;

import lombok.Getter;

@Getter
public class PlaceIdDTO {
    private String placeId;
    private int sequence;

    public PlaceIdDTO(String placeId, int sequence) {
        this.placeId = placeId;
        this.sequence = sequence;
    }
}
