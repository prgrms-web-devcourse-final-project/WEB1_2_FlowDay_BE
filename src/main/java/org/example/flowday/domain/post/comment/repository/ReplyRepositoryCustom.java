package org.example.flowday.domain.post.comment.repository;

import org.example.flowday.domain.post.comment.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReplyRepositoryCustom {
    List<Reply> findAllReplies(Long postId);
}
