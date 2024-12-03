package org.example.flowday.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class MemberDTO {

    @Data
    public static class CreateRequestDTO {
        @NotBlank(message = "로그인 ID는 필수 입력 값 입니다")
        private String loginId;
        @NotBlank(message = "이메일은 필수 입력 값 입니다")
        private String email;
        @NotBlank(message = "비밀번호는 필수 입력 값 입니다")
        private String pw;

        public Member toEntity() {
            return Member.builder()
                    .loginId(loginId)
                    .email(email)
                    .pw(pw)
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
    }

    @Data
    @NoArgsConstructor
    public static class LoginRequestDTO {
        private String loginId;
        private String pw;
    }

    @Data
    @AllArgsConstructor
    public static class UpdateRequestDTO {
        private String loginId;
        private String email;
        private String pw;
        private String name;

        public Member toEntity() {
            return Member.builder()
                    .loginId(loginId)
                    .email(email)
                    .pw(pw)
                    .name(name)
                    .build();
        }
    }

    @Data
    public static class TokenRefreshRequestDTO {
        private String token;
    }

    @Data
    @AllArgsConstructor
    public static class TokenRefreshResponseDTO {
        private String token;
    }

    @Data
    @AllArgsConstructor
    public static class UpdateResponseDTO {
        private String loginId;
        private String email;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class MyInfoSettingRequestDTO {
        private String name;
        private MultipartFile file;
        private LocalDate birthDt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyPageResponseDTO {
        private String profileImage;
        private String name;
        private String partnerImage;
        private String partnerName;
        private LocalDate relationshipDt;
        private LocalDate birthDt;
    }

    @Data
    @AllArgsConstructor
    public static class ReadResponseDTO {
        private String name;
        private String profileImage;
        private Long partnerId;
        private LocalDate relationshipDt;
        private LocalDate birthDt;
    }

    @Data
    @AllArgsConstructor
    public static class ChangeImageResponseDTO{
        private Long id;
        private String mImage;
    }

    @Data
    public static class FindIdRequestDTO {
        private String name;
        private String email;
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

    @Data
    public static class UpdateBirthdayRequestDTO {
        private LocalDate birthDt;
    }

    @Data
    public static class SendCoupleRequestDTO {
        private Long partnerId;
        private String relationshipDt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor  // Lombok의 @NoArgsConstructor 추가
    public static class UpdatePartnerIdRequestDTO {

        private Long senderId;
        private String relationshipDt;
    }


    @Data
    @AllArgsConstructor
    public static class FindPartnerResponseDTO {
        private Long id;
        private String profileImage;
        private String name;
        private String email;

    }
}
