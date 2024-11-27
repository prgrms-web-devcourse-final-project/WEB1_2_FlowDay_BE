package org.example.flowday.domain.post.comment.comment.repository;

import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> , ReplyRepositoryCustom{

    @Query("SELECT DISTINCT r.post.id FROM Reply r WHERE r.member.id = :memberId")
    List<Long> findAllPostIdByMemberId(Long memberId);


}
