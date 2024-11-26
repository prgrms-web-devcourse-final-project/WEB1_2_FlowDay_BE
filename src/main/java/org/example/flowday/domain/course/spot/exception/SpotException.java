package org.example.flowday.domain.course.spot.exception;

import org.springframework.http.HttpStatus;

public enum SpotException {
    NOT_FOUND("장소를 찾을 수 없습니다.",  HttpStatus.NOT_FOUND),
    NOT_DELETED("장소를 삭제하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    NOT_CREATED("장소를 생성하는 데 실패했습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

    SpotException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public SpotTaskException get() {
        return new SpotTaskException(this.message, this.status);
    }
}
