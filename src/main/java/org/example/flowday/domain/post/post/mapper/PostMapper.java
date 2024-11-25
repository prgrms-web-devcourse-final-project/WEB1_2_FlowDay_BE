package org.example.flowday.domain.post.post.mapper;

import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostMapper {

    public Post toEntity(PostRequestDTO DTO , Member writer , Course course) {
        return Post.builder()
                .writer(writer)
                .city(DTO.getCity())
                .title(DTO.getTitle())
                .contents(DTO.getContents())
                .course(course)
                .status(DTO.getStatus())
                .build();
    }

    public PostResponseDTO toResponseDTO(Post post , List<SpotResDTO> spots) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .writerName(post.getWriter().getName())
                .city(post.getCity())
                .title(post.getTitle())
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .spots(spots)
                .build();
    }
}