package org.example.flowday.domain.post.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.QMember;
import org.example.flowday.domain.post.likes.entity.QLikes;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.entity.QPost;
import org.example.flowday.domain.post.post.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> searchLatestPost(Pageable pageable) {
        QPost post = QPost.post;

        List<Post> content = queryFactory.
                selectFrom(post)
                .where(post.status.eq(Status.PUBLIC))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(post)
                .fetch()
                .size();

        return new PageImpl<>(content, pageable, total);


    }

    @Override
    public Page<Post> searchMostLikedPost(Pageable pageable) {
        QPost post = QPost.post;
        QLikes likes = QLikes.likes; // Likes 테이블의 Q타입

        // 좋아요 수를 카운트하여 내림차순 정렬
        List<Post> content = queryFactory
                .selectFrom(post)
                .leftJoin(likes).on(likes.postId.eq(post.id)) // Post와 Likes를 조인
                .where(post.status.eq(Status.PUBLIC))
                .groupBy(post.id) // Post별로 그룹화
                .orderBy(likes.count().desc()) // 좋아요 수 기준 내림차순 정렬
                .offset(pageable.getOffset()) // 페이지 시작점
                .limit(pageable.getPageSize()) // 페이지 크기 제한
                .fetch(); // 결과 조회

        // 전체 게시글 수 조회
        long total = queryFactory
                .selectFrom(post)
                .leftJoin(likes).on(likes.postId.eq(post.id))
                .where(post.status.eq(Status.PUBLIC))
                .groupBy(post.id)
                .fetchCount(); // 게시글 수 카운트

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public Page<Post> searchCouplePost(Pageable pageable , Long memberId , Long partnerId) {
        QPost post = QPost.post;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.status.eq(Status.COUPLE)); // 상태가 COUPLE인 게시글만 필터링

        // 작성자가 현재 회원인 경우 또는 파트너인 경우 필터링
        builder.and(
                post.writer.id.eq(memberId)
                        .or(partnerId != null ? post.writer.id.eq(partnerId) : null)
        );

        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(builder)
                .orderBy(post.createdAt.desc()) // 최신순으로 정렬
                .offset(pageable.getOffset()) // 페이지 시작점
                .limit(pageable.getPageSize()) // 페이지 크기 제한
                .fetch(); // 결과 조회

        return new PageImpl<>(posts, pageable, posts.size());




    }

    @Override
    public Page<Post> searchPrivatePost(Pageable pageable, Long userId) {
        QPost post = QPost.post;


        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(
                        post.status.eq(Status.PRIVATE)
                                .and(post.writer.id.eq(userId))
                )
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        return new PageImpl<>(posts, pageable, posts.size());


    }

    @Override
    public Page<Post> searchMyPost(Pageable pageable, Long memberId) {
        QPost post = QPost.post;

        BooleanBuilder builder = new BooleanBuilder();

        // 작성자가 현재 회원인 경우
        builder.and(post.writer.id.eq(memberId));

        // 쿼리로 게시글 목록 조회
        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(builder)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 게시글 수 조회
        long total = queryFactory
                .selectFrom(post)
                .fetch()
                .size();

        return new PageImpl<>(posts, pageable, total);
    }


    @Override
    public Page<Post> searchMyPost(Pageable pageable, List<Long> postIds) {
        QPost post = QPost.post;

        BooleanBuilder builder = new BooleanBuilder();

        // 특정 ID들의 게시글 조회
        builder.and(post.id.in(postIds));

        // 쿼리로 게시글 목록 조회
        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(builder)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 게시글 수 조회
        long total = queryFactory
                .selectFrom(post)
                .fetch()
                .size();

        return new PageImpl<>(posts, pageable, total);
    }


}
