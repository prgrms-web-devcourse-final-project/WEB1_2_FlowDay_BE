package org.example.flowday.domain.course.course.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CourseTaskException extends RuntimeException {
    private final HttpStatus status;

    public CourseTaskException(String message,HttpStatus status) {
        super(message);
        this.status = status;
    }
}
