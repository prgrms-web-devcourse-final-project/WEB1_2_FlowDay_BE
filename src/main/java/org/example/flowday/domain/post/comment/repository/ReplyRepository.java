package org.example.flowday.domain.post.comment.repository;

import org.example.flowday.domain.post.comment.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> , ReplyRepositoryCustom{
}
