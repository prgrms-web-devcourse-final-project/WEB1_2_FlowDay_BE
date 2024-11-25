package org.example.flowday.domain.post.tag.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDTO {
    private Long id;
    private String name;
}