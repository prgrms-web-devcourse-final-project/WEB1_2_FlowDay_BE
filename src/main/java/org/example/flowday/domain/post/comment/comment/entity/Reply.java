package org.example.flowday.domain.post.comment.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private int likeCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reply parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Reply> children = new ArrayList<>();


    @Builder
    public Reply(Post post, String content, Member member, Reply parent) {
        setPost(post);
        this.content = content;
        this.member = member;
        this.likeCount = 0;
        setParent(parent);
    }

    /*
     ** 연관관계 편의 메서드
     */
    public void setParent(Reply parent) {
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    public void setPost(Post post) {
        this.post = post;
        post.getReplys().add(this);
    }


    public void updateContent(String content) {
        this.content = content;
    }

    public void updateDeleteMsg() {
        this.content = "작성자에 의해 삭제되었습니다";
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }
    public void decreaseLikeCount() {
        this.likeCount--;
    }

}
