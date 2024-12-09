package org.example.flowday.domain.course.course.dto;

import lombok.Getter;
import org.example.flowday.domain.course.course.entity.Course;
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

    public CourseListResDTO(Course course, List<PlaceIdDTO> spots) {
        this.id = course.getId();
        this.memberId = course.getMember().getId();
        this.title = course.getTitle();
        this.status = course.getStatus();
        this.date = course.getDate();
        this.color = course.getColor();
        this.spots = spots;
    }

}
