package org.example.flowday.domain.course.vote.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class VoteTaskException extends RuntimeException {
    private final HttpStatus status;

    public VoteTaskException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
