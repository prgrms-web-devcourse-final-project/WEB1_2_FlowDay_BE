package org.example.flowday.domain.post.tag.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.post.entity.Post;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Builder
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    private String content;


}
