package org.example.flowday.domain.course.course.exception;

import org.springframework.http.HttpStatus;

public enum CourseException {
    NOT_FOUND("코스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_CREATED("코스를 생성하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    NOT_UPDATED("코스를 수정하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    NOT_DELETED("코스를 삭제하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN("권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;

    CourseException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public CourseTaskException get() {
        return new CourseTaskException(this.message, this.status);
    }
}
