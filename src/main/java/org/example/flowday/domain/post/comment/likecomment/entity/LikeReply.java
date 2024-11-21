package org.example.flowday.domain.post.comment.likecomment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.comment.comment.entity.Reply;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class LikeReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Reply reply;

    @Builder
    public LikeReply(Member member, Reply reply) {
        this.member = member;
        this.reply = reply;
    }
}
