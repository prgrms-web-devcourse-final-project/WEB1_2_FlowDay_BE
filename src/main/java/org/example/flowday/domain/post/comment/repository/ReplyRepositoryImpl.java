package org.example.flowday.domain.post.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.QMember;
import org.example.flowday.domain.post.comment.entity.QReply;
import org.example.flowday.domain.post.comment.entity.Reply;
import org.example.flowday.domain.post.post.entity.QPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Reply> findAllReplies(Long postId) {
        QReply reply = QReply.reply;
        QReply parentReply = new QReply("parentReply");
        QMember member = QMember.member;
        QPost post = QPost.post;

        // 댓글을 시간 오름차순으로 모두 조회
        List<Reply> replies = queryFactory.selectFrom(reply)
                .leftJoin(reply.member, member).fetchJoin()
                .leftJoin(reply.post, post).fetchJoin()
                .leftJoin(reply.parent, parentReply).fetchJoin()
                .where(reply.post.id.eq(postId))
                .orderBy(reply.createdAt.asc())
                .distinct()
                .fetch();

        return replies;
    }
}
