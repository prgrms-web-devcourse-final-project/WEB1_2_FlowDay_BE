package org.example.flowday.domain.course.vote.exception;

import org.springframework.http.HttpStatus;

public enum VoteException {
    NOT_FOUND("투표를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_CREATED("투표를 생성하는 데 실패했습니다.",  HttpStatus.BAD_REQUEST),
    NOT_UPDATED("투표를 수정하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    NOT_DELETED("투표를 삭제하는 데 실패했습니다.",  HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

    VoteException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public VoteTaskException get() {
        return new VoteTaskException(this.message, this.status);
    }
}
