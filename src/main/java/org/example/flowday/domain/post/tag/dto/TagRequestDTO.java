package org.example.flowday.domain.post.tag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequestDTO {
    @NotBlank(message = "태그 이름은 필수입니다.")
    private String name;
}