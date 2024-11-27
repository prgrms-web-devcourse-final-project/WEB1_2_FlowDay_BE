package org.example.flowday.domain.post.post.exception;

import org.springframework.http.HttpStatus;

public enum PostException {
    POST_NOT_FOUND("해당 게시글을 찾을 수 없습니다" , HttpStatus.BAD_REQUEST),
    POST_NOT_CREATED("코스를 생성하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    POST_NOT_UPDATED("코스를 수정하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    POST_NOT_DELETED("코스를 삭제하는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    POST_FORBIDDEN("게시글 작성자만 수정, 삭제 할 수 있습니다", HttpStatus.FORBIDDEN),
    POST_IS_LIKE("게시글에 이미 좋아요를 눌렀습니다", HttpStatus.BAD_REQUEST );

    private final String message;
    private final HttpStatus status;

    PostException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public PostTaskException get() {
        return new PostTaskException(this.message, status);
    }

}
