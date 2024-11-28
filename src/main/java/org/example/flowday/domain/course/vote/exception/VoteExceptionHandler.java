package org.example.flowday.domain.course.vote.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class VoteExceptionHandler {

    @ExceptionHandler(VoteTaskException.class)
    public ResponseEntity<?> handleVoteTaskException(VoteTaskException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(Map.of(
                        "status", e.getStatus().value(),
                        "error", e.getMessage()
                ));
    }
}
