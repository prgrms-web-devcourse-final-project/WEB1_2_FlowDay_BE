package org.example.flowday.domain.course.course.dto;

import lombok.Getter;
import org.example.flowday.domain.course.course.entity.Status;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CourseListResDTO {
    private Long id;
    private Long memberId;
    private String title;
    private Status status;
    private LocalDate date;
    private String color;
    private List<PlaceIdDTO> spots;

    public CourseListResDTO(Long id, Long memberIdFromCourse, String title, Status status, LocalDate date, String color, List<PlaceIdDTO> spots) {
        this.id = id;
        this.memberId = memberIdFromCourse;
        this.title = title;
        this.status = status;
        this.date = date;
        this.color = color;
        this.spots = spots;
    }

}
