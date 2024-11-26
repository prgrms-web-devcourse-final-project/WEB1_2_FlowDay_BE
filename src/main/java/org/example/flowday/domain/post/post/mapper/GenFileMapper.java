package org.example.flowday.domain.post.post.mapper;

import org.example.flowday.domain.post.post.dto.GenFileResponseDTO;
import org.example.flowday.global.fileupload.entity.GenFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class GenFileMapper {
    public static GenFileResponseDTO toResponseDTO(GenFile genFile) {
        // 파일 URL 생성
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/")
                .path(String.valueOf(genFile.getId()))
                .toUriString();

        return GenFileResponseDTO.builder()
                .id(genFile.getId())
                .url(fileUrl)
                .originFileName(genFile.getOriginFileName())
                .fileSize(genFile.getFileSize())
                .fileExt(genFile.getFileExt())
                .build();
    }
}
