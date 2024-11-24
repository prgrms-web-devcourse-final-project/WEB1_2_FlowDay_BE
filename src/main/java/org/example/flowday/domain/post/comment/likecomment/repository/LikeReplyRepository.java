package org.example.flowday.domain.post.comment.likecomment.repository;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.likecomment.entity.LikeReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeReplyRepository extends JpaRepository<LikeReply, Long> {

     Optional<LikeReply> findByReplyAndMember(Reply reply, Member member);

}
