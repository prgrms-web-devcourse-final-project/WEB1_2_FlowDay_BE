package org.example.flowday.domain.post.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.flowday.domain.post.post.entity.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequestDTO {
    @NotBlank(message = "도시 정보는 필수입니다.")
    private String city;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String contents;

    private Status status;

    private List<MultipartFile> images;
}
