package org.example.flowday.domain.course.wish.exception;

import org.springframework.http.HttpStatus;

public enum WishPlaceException {
    NOT_FOUND("위시 플레이스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN("권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;

    WishPlaceException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public WishPlaceTaskException get() {
        return new WishPlaceTaskException(this.message, this.status);
    }
}
