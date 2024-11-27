package org.example.flowday.domain.post.post.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PostTaskException extends RuntimeException {
    private final HttpStatus status;

    public PostTaskException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
