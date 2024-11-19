package org.example.flowday.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.flowday.domain.member.entity.Member;

import java.util.Map;

public class MemberDTO {

    @Data
    public static class StringResponseDTO {
        private Map<String, String> response;
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
    public static class LoginRequestDTO {
        private String loginId;
        private String password;
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

    public static class ReadResponseDTO {

    }
}
