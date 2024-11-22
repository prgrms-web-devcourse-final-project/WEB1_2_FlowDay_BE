package org.example.flowday.domain.post.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.course.entity.Course;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.tag.entity.Tag;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String city;

    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @OneToOne()
    @JoinColumn(name="course_id" , referencedColumnName = "id")
    private Course course;

    @OneToMany(mappedBy = "post" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Reply> replys = new ArrayList<>();


    @Builder
    public Post(String title, String city, String content, Status status, Member writer, Course course) {
        this.title = title;
        this.city = city;
        this.content = content;
        this.status = status;
        this.writer = writer;
        this.course = course;
    }
}
