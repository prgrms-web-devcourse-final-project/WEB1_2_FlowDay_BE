package org.example.flowday.domain.post.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.entity.QCourse;
import org.example.flowday.domain.course.spot.entity.QSpot;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.QMember;

import org.example.flowday.domain.post.comment.comment.entity.QReply;
import org.example.flowday.domain.post.likes.entity.QLikes;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.entity.QPost;
import org.example.flowday.domain.post.post.entity.Status;
import org.example.flowday.domain.post.tag.entity.QPostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    //게시글 최신순 조회
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

    //좋아요 순 게시글 조회
    @Override
    public Page<Post> searchMostLikedPost(Pageable pageable) {
        QPost post = QPost.post;
        QLikes likes = QLikes.likes;


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


    //커플 게시글 조회
    @Override
    public Page<Post> searchCouplePost(Pageable pageable, Long memberId, Long partnerId) {
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

    //개인 PRIVATE 게시글 조회
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

    //내가 작성한 게시글들 조회
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

    //내가 좋아요 누른 게시글 조회
    @Override
    public Page<Post> searchMyLikePost(Pageable pageable, List<Long> postIds) {
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

    @Override
    public Page<Post> searchMyReplyPost(Pageable pageable, Long memberId) {
        QPost post = QPost.post;
        QReply reply = QReply.reply;

        List<Post> posts = queryFactory
                .selectDistinct(post)
                .from(post)
                .join(post.replies, reply)
                .where(reply.member.id.eq(memberId))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectDistinct(post)
                .from(post)
                .join(post.replies, reply)
                .where(reply.member.id.eq(memberId))
                .fetchCount();

        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<Post> searchKwPost(Pageable pageable, String kw) {
        // Q 클래스 인스턴스 생성
        QPost post = QPost.post;
        QPostTag postTag = QPostTag.postTag;
        QCourse course = QCourse.course;
        QSpot spot = QSpot.spot;
        QMember writer = QMember.member;

        // 검색어를 소문자로 변환하여 대소문자 구분 없이 검색합니다.
        String lowerKw = kw.toLowerCase();

        // 조건을 담을 리스트 생성
        List<BooleanExpression> conditions = new ArrayList<>();

        if (kw != null && !kw.isBlank()) {

            // 제목에 키워드가 포함되는지 검사
            conditions.add(post.title.lower().contains(lowerKw));

            // 내용에 키워드가 포함되는지 검사
            conditions.add(post.contents.lower().contains(lowerKw));

            // 작성자의 이름에 키워드가 포함되는지 검사
            conditions.add(post.writer.name.lower().contains(lowerKw));

            // 지역에 키워드가 포함되는지 검사
            conditions.add(post.region.lower().contains(lowerKw));

            // 계절에 키워드가 포함되는지 검사
            conditions.add(post.season.lower().contains(lowerKw));

            // 태그에 키워드가 존재하는지 검사 (EXISTS 서브쿼리 사용)
            BooleanExpression tagExists = JPAExpressions.selectOne()
                    .from(postTag)
                    .where(postTag.content.lower().contains(lowerKw)
                            .and(postTag.post.eq(post)))
                    .exists();
            conditions.add(tagExists);

            // 코스 이름에 키워드가 포함되는지 검사
            conditions.add(post.course.title.lower().contains(lowerKw));

            // 장소 이름에 키워드가 존재하는지 검사 (EXISTS 서브쿼리 사용)
            BooleanExpression placeExists = JPAExpressions.selectOne()
                    .from(spot)
                    .where(spot.name.lower().contains(lowerKw)
                            .and(spot.course.eq(post.course)))
                    .exists();
            conditions.add(placeExists);
        }

        // 조건들을 OR로 결합
        BooleanExpression predicate = conditions.stream()
                .reduce(BooleanExpression::or)
                .orElse(null);

        // 상태가 'public'인 게시글만 조회하기 위한 조건 추가
        BooleanExpression statusCondition = post.status.eq(Status.PUBLIC);

        // 최종 조건 결합
        BooleanExpression finalCondition = statusCondition;

        if (predicate != null) {
            finalCondition = finalCondition.and(predicate);
        }

        // 쿼리 생성
        List<Post> posts = queryFactory.selectFrom(post)
                .leftJoin(post.writer, writer)
                .leftJoin(post.course, course)
                .where(finalCondition)
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 결과 수 조회
        long total = queryFactory.select(post.count())
                .from(post)
                .leftJoin(post.writer, writer)
                .leftJoin(post.course, course)
                .where(finalCondition)
                .fetchOne();

        // 페이지 생성 및 반환
        return new PageImpl<>(posts, pageable, total);
    }


}
