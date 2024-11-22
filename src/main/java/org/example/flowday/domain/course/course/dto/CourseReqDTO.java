package org.example.flowday.domain.course.course.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class CourseReqDTO {
    private Long memberId;
    private String title;
    private Status status;
    private LocalDate date;
    private String color;
    private List<SpotReqDTO> spots;
}