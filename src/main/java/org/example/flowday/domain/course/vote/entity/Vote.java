package org.example.flowday.domain.course.vote.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.spot.entity.Spot;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Course cannot be null")
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Spot> spots = new ArrayList<>();
}

