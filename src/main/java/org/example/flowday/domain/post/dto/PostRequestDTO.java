package org.example.flowday.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDTO {
    @NotNull(message = "Member ID는 필수입니다.")
    private Long memberId;

    @NotBlank(message = "도시 정보는 필수입니다.")
    private String city;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String contents;

    @NotNull(message = "Course ID는 필수입니다.")
    private Long courseId;
}