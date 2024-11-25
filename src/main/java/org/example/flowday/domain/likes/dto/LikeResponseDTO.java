package org.example.flowday.domain.likes.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDTO {
    private Long id;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
}