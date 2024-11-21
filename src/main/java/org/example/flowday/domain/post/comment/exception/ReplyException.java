package org.example.flowday.domain.post.comment.exception;

import org.springframework.http.HttpStatus;

public enum ReplyException {
    REPLY_NOT_FOUND("해당 댓글을 찾을 수 없습니다" , HttpStatus.BAD_REQUEST);


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
