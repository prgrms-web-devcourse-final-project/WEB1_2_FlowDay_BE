package org.example.flowday.domain.post.mapper;

import org.example.flowday.domain.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(PostRequestDTO DTO) {
        return Post.builder()
                .memberId(DTO.getMemberId())
                .city(DTO.getCity())
                .title(DTO.getTitle())
                .contents(DTO.getContents())
                .courseId(DTO.getCourseId())
                .build();
    }

    public PostResponseDTO toResponseDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .memberId(post.getMemberId())
                .city(post.getCity())
                .title(post.getTitle())
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .courseId(post.getCourseId())
                .build();
    }
}