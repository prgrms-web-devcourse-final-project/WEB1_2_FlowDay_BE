package org.example.flowday.domain.post.post.repository;


import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PostRepositoryCustom {
    Page<Post> searchLatestPost(Pageable pageable);
}
