package org.example.flowday.domain.tag.dto;

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