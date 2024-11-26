package org.example.flowday.domain.post.post.dto;

import lombok.*;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    private Long id;
    private String writerName;
    private String city;
    private String title;
    private String contents;
    private Long courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SpotResDTO> spots;
    private List<GenFileResponseDTO> images;
}