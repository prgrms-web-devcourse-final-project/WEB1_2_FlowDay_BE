package org.example.flowday.domain.post.post.dto;

import lombok.*;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.post.post.entity.Status;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    private Long id;
    private String nickName;
    private String region;
    private String season;
    private String title;
    private String contents;
    private String tags;
    private Status status;
    private Long courseId;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SpotResDTO> spots;
    private List<GenFileResponseDTO> images;
}