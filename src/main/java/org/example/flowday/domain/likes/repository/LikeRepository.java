package org.example.flowday.domain.likes.repository;

import org.example.flowday.domain.likes.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    boolean existsLike(Long userId, Long postId);
    void deleteLike(Long userId, Long postId);
}