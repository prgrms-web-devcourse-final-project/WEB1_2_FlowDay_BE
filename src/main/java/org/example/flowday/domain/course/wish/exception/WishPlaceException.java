package org.example.flowday.domain.course.wish.exception;

import org.springframework.http.HttpStatus;

public enum WishPlaceException {
    NOT_FOUND("위시 플레이스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_UPDATED("위시 플레이스에 장소를 추가하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    NOT_DELETED("위시 플레이스의 장소를 삭제하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
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
