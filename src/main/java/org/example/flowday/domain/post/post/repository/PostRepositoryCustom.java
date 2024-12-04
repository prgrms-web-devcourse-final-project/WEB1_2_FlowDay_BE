package org.example.flowday.domain.post.post.repository;


import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PostRepositoryCustom {
    Page<Post> searchLatestPost(Pageable pageable);

    Page<Post> searchMostLikedPost(Pageable pageable);

    Page<Post> searchCouplePost(Pageable pageable , Long memberId , Long partnerId);

    Page<Post> searchMyPost(Pageable pageable , Long memberId);

    Page<Post> searchMyLikePost(Pageable pageable , List<Long> postIds);

    Page<Post> searchPrivatePost(Pageable pageable, Long memberId);

    Page<Post> searchMyReplyPost(Pageable pageable, Long memberId);

    Page<Post> searchKwPost(Pageable pageable, String kw);

}
