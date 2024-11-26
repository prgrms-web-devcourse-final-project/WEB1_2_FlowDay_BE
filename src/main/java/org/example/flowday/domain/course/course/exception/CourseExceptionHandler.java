package org.example.flowday.domain.course.course.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CourseExceptionHandler {

    @ExceptionHandler(CourseTaskException.class)
    public ResponseEntity<?> handleCourseTaskException(CourseTaskException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(Map.of(
                        "status", e.getStatus().value(),
                        "error", e.getMessage()
                ));
    }
}
