package org.example.flowday.domain.post.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.comment.entity.Reply;
import org.example.flowday.domain.post.post.entity.Post;

import java.time.LocalDateTime;

public class ReplyDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createRequest {
        private String content;
        private Long postId;
        private Long parentId;


        public Reply toEntity(Member member ,Reply parent,  Post post) {
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
        private Long parentId;
        private Long postId;
        private LocalDateTime createdAt;

        public createResponse (Reply reply , String msg) {
            this.msg = msg;
            this.content = reply.getContent();
            this.memberName=reply.getMember().getName();
            if (reply.getParent() != null) {
                parentId = reply.getParent().getId();
            } else {
                parentId = null;
            }
            this.postId=reply.getPost().getId();
            this.createdAt=reply.getCreatedAt();
        }


    }


    @Getter
    @NoArgsConstructor
    public static class updateRequest {
        private String content;
        private Long replyId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class updateResponse {
        private String msg;
        private String content;




    }
}
