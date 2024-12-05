package org.example.flowday.domain.post.post.dto;


import lombok.*;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.entity.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostBriefResponseDTO {
    private Long id;
    private String title;
    private String content;
    private int commentCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private String imageURL;
    private Status status;
    private String nickName;
    private String tags;


    public PostBriefResponseDTO(Post post , String url) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContents().length() > 20
                ? post.getContents().substring(0, 20)
                : post.getContents();
        commentCount = post.getReplies().size();
        likeCount = post.getLikeCount();
        createdAt = post.getCreatedAt();
        status = post.getStatus();
        nickName=post.getWriter().getName();
        imageURL = url;
        tags=post.getTagStr();


    }


}
