package org.example.flowday.domain.course.spot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.vote.entity.Vote;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double latitude;
    private double longitude;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String comment;
    private int sequence;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "vote_id", nullable = true)
    private Vote vote;
}

