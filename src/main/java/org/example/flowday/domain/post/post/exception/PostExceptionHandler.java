package org.example.flowday.domain.post.post.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PostExceptionHandler {

    @ExceptionHandler(PostTaskException.class)
    public ResponseEntity<?> handlePostNotFoundException(PostTaskException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(Map.of(
                        "status", e.getStatus().value(),
                        "error", e.getMessage()
                ));
    }

}