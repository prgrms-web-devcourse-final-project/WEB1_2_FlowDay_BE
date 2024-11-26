package org.example.flowday.domain.course.wish.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class WishPlaceTaskException extends RuntimeException {
    private final HttpStatus status;

    public WishPlaceTaskException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
