package org.example.flowday.domain.post.post.entity;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.member.entity.Member;
//import org.example.flowday.domain.post.likes.entity.LikeEntity;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
//import org.example.flowday.domain.post.tag.entity.Tag;
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.tag.entity.PostTag;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String region;

    private String season;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int likeCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @OneToOne(fetch = FetchType.LAZY)
    private Course course;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<PostTag> tags = new ArrayList<>() ;


    public void remove() {
        writer.getPosts().remove(this);
        writer=null;
    }

    public void increaseLike() {
        likeCount++;
    }

    public void decreaseLike() {
        likeCount--;
    }


    public void addTag(String tagContent) {
        PostTag tag = PostTag.builder()
                .post(this)
                .member(writer)
                .content(tagContent)
                .build();
        tags.add(tag);
    }


    public void addTag(String ... tagContents) {
        for(String tagContent : tagContents) {
            addTag(tagContent);
        }
    }

    public String getTagStr() {
        String tagsStr = tags
                .stream()
                .map(PostTag::getContent)
                .collect(Collectors.joining(" #"));

        if (tagsStr.isBlank()) {
            return "";
        }
        return "#"+tagsStr;

    }

    public void updatePost(PostRequestDTO request) {
        region = request.getRegion();
        season=request.getSeason();
        title = request.getTitle();
        contents = request.getContents();
        status = request.getStatus();

    }



}
