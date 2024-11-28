package org.example.flowday.domain.post.post.dto;


import lombok.*;
import org.example.flowday.domain.post.post.entity.Post;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBriefResponseDTO {
    private Long id;
    private String title;
    private String content;
    private int commentCount;
    private LocalDateTime createdAt;
    private String imageURL;


    public PostBriefResponseDTO(Post post , String url) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContents().length() > 20
                ? post.getContents().substring(0, 20)
                : post.getContents();
        commentCount = post.getReplies().size();
        createdAt = post.getCreatedAt();
        imageURL = url;


    }


}
