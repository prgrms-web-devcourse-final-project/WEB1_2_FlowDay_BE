package org.example.flowday.domain.post.tag.repository;

import org.example.flowday.domain.post.tag.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findByMemberId(long writerId);

    List<PostTag> findByMember_Name(String writerName);
}