package org.example.flowday.domain.vote.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.course.entity.Course;
import org.example.flowday.domain.spot.entity.Spot;

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

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String title;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spot> spots = new ArrayList<>();
}

