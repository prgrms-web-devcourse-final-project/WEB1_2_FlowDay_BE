package org.example.flowday.domain.course.wish.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class WishPlaceExceptionHandler {

    @ExceptionHandler(WishPlaceTaskException.class)
    public ResponseEntity<?> handleWishPlaceTaskException(WishPlaceTaskException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(Map.of(
                        "status", e.getStatus().value(),
                        "error", e.getMessage()
                ));
    }
}
