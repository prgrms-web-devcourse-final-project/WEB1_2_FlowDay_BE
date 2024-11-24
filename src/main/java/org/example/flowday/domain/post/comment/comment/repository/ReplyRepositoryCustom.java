package org.example.flowday.domain.post.comment.comment.repository;

import org.example.flowday.domain.post.comment.comment.entity.Reply;

import java.util.List;

public interface ReplyRepositoryCustom {
    List<Reply> findAllReplies(Long postId);
}
