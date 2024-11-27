package org.example.flowday.domain.post.post.repository;

import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> , PostRepositoryCustom {
    Page<Post> findAllByIdIn(List<Long> postIds, Pageable pageable);
}