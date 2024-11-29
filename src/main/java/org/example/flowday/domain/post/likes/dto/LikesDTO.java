package org.example.flowday.domain.post.likes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public class LikesDTO {

    @Data
    public static class LikeRequestDTO {

        private Long postId;

    }

    @Data
    @AllArgsConstructor
    public static class LikeResponseDTO {

        private Long id;
        private Long userId;
        private Long postId;
        private String msg;

    }
}
