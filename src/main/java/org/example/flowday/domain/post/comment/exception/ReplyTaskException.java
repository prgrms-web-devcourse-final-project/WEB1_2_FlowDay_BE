package org.example.flowday.domain.post.comment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReplyTaskException extends RuntimeException {
    private final HttpStatus status;

    public ReplyTaskException(String message,HttpStatus status) {
        super(message);
        this.status = status;
    }
}
