package org.example.flowday.domain.post.post.repository;


import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostRepositoryCustom {
    Page<Post> searchLatestPost(Pageable pageable);

    Page<Post> searchCouplePost(Pageable pageable , Long memberId , Long partnerId);
}
