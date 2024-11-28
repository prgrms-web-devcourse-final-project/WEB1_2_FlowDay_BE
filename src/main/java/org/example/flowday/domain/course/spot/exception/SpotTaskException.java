package org.example.flowday.domain.course.spot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class SpotTaskException extends RuntimeException {
    private final HttpStatus status;

    public SpotTaskException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
