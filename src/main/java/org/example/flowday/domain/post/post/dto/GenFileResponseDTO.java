package org.example.flowday.domain.post.post.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenFileResponseDTO {
    private Long id;
    private String url;
    private String originFileName;
    private int fileSize;
    private String fileExt;


}
