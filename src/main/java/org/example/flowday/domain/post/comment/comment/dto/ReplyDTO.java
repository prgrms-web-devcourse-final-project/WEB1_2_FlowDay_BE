package org.example.flowday.domain.post.comment.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.post.entity.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReplyDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createRequest {
        @NotBlank(message = "댓글 내용을 작성해주세요")
        @Size(min = 1, max = 500, message = "내용은 1자에서 500자 사이여야 합니다.")
        private String content;
        private Long parentId;


        public Reply toEntity(Member member, Reply parent, Post post) {
            return Reply.builder()
                    .content(content)
                    .member(member)
                    .parent(parent)
                    .post(post)
                    .build();
        }

    }

    @Getter
    @NoArgsConstructor
    public static class createResponse {
        private String msg;
        private String content;
        private String memberName;
        private int likeCount;
        private Long parentId;
        private Long postId;
        private Long replyId;
        private LocalDateTime createdAt;

        public createResponse(Reply reply, String msg) {
            this.msg = msg;
            this.content = reply.getContent();
            this.memberName = reply.getMember().getName();
            if (reply.getParent() != null) {
                parentId = reply.getParent().getId();
            } else {
                parentId = null;
            }
            this.postId = reply.getPost().getId();
            this.createdAt = reply.getCreatedAt();
            this.replyId = reply.getId();
            this.likeCount = reply.getLikeCount();
        }


    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class updateRequest {
        @NotBlank(message = "수정 할 댓글 내용을 작성해주세요")
        @Size(min = 1, max = 500, message = "내용은 1자에서 500자 사이여야 합니다.")
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class updateResponse {
        private String msg;
        private String content;


    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String content;
        private String memberName;
        private int likeCount;
        private LocalDateTime createdAt;
        private String memberImgURL;
        private List<Response> children;

        public Response(Reply reply , String imageURL) {
            id = reply.getId();
            content = reply.getContent();
            memberName = reply.getMember().getName();
            likeCount = reply.getLikeCount();
            createdAt = reply.getCreatedAt();
            memberImgURL = imageURL;
            children = new ArrayList<>();

        }

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class deleteResponse {
        private String msg;
        private String content;
    }
}
