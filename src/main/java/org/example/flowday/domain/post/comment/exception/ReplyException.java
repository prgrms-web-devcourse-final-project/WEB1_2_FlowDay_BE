package org.example.flowday.domain.post.comment.exception;

import org.springframework.http.HttpStatus;

public enum ReplyException {
    REPLY_NOT_FOUND("해당 댓글을 찾을 수 없습니다" , HttpStatus.BAD_REQUEST),
    REPLY_AUTHORITED("댓글 작성자만 수정,삭제 할 수 있습니다", HttpStatus.FORBIDDEN);


    private final String message;
    private final HttpStatus status;

    ReplyException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public ReplyTaskException getReplyException() {
        return new ReplyTaskException(this.message, status);
    }

}
