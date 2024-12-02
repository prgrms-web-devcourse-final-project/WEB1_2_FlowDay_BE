package org.example.flowday.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.exception.MemberTaskException;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.post.entity.Post;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.xml.stream.events.Comment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String pw;
    private String email;
    @Column(unique = true)
    private String name;
    private Long partnerId;
    private Long chattingRoomId;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;
    private Role role;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate relationshipDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate birthDt;

    @OneToMany(mappedBy = "member")
    private List<Course> courses;

    @OneToMany(mappedBy = "member")
    private List<Reply> reply = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();


    public void filterAndValidate(Member member) throws MemberTaskException {
        if( !isValidCharacters(member.loginId) ) {
            throw MemberException.INVALID_CHAR_FORMAT.getMemberTaskException();
        }
        if (!isValidEmail(member.email)) {
            throw MemberException.INVALID_EMAIL_FORMAT.getMemberTaskException();
        }
    }

    // 특수문자 필터링 메소드
    private Boolean isValidCharacters(String input) {
        if (input != null) {
            // 특수문자 필터링: 알파벳, 숫자, @, ., -, _, +, 공백만 허용
            return !input.matches(".*[^a-zA-Z0-9@._ -].*");
        }
        return false;
    }

    // 이메일 유효성 검사
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
