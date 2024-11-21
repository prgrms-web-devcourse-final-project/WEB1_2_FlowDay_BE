package org.example.flowday.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

public class MemberDTO {

    @Data
    public static class StringResponseDTO {
        private String response;
        public StringResponseDTO(String response) {
            this.response = response;
        }
    }

    @Data
    public static class CreateRequestDTO {
        @NotBlank(message = "로그인 ID는 필수 입력 값 입니다")
        private String loginId;
        @NotBlank(message = "이메일은 필수 입력 값 입니다")
        private String email;
        @NotBlank(message = "비밀번호는 필수 입력 값 입니다")
        private String pw;
        @NotBlank(message = "닉네임은 필수 입력 값 입니다")
        private String name;
        @NotBlank(message = "전화번호는 필수 입력 값 입니다")
        private String phoneNum;

        public Member toEntity() {
            return Member.builder()
                    .loginId(loginId)
                    .email(email)
                    .pw(pw)
                    .name(name)
                    .phoneNum(phoneNum)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    public static class CreateResponseDTO {
        private Long id;
        private String loginId;
        private String email;
        private String name;
        private String phoneNum;
    }

    @Data
    @NoArgsConstructor
    public static class LoginRequestDTO {
        private String loginId;
        private String pw;
    }

    @Data
    public static class UpdateRequestDTO {
        private String loginId;
        private String email;
        private String pw;
        private String name;
        private String phoneNum;

        public Member toEntity() {
            return Member.builder()
                    .loginId(loginId)
                    .email(email)
                    .pw(pw)
                    .name(name)
                    .phoneNum(phoneNum)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    public static class ReadResponseDTO {
        private String profileImage;
        private String name;
        private Long partnerId;
        private LocalDateTime dateOfRelationshipStart;
        private LocalDateTime dateOfBirth;
    }

    @Data
    @AllArgsConstructor
    public static class ChangeImageResponseDTO{
        private Long id;
        private String mImage;
    }

    @Data
    @AllArgsConstructor
    public static class FindIdResponseDTO {
        private String loginId;
    }

    @Data
    public static class FindPWRequestDTO {
        private String loginId;
        private String email;
    }
}
