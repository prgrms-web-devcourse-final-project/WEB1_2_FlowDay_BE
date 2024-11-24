package org.example.flowday.domain.course.vote.dto;

import lombok.*;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;

import java.util.List;

@Builder
@Getter
public class VoteReqDTO {
    private Long courseId;
    private String title;
    private List<SpotReqDTO> spots;
}

