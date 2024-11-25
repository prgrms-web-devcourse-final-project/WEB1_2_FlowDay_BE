package org.example.flowday.domain.likes.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRequestDTO {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "게시물 ID는 필수입니다.")
    private Long postId;
}