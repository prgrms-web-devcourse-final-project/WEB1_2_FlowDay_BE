package org.example.flowday.domain.post.post.mapper;

import org.example.flowday.domain.post.post.dto.GenFileResponseDTO;
import org.example.flowday.global.fileupload.entity.GenFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class GenFileMapper {
    public static GenFileResponseDTO toResponseDTO(GenFile genFile) {
        // S3에 저장된 파일의 URL을 사용하여 파일 URL 생성
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s/%s",
                "flowday", // 버킷 이름
                "ap-northeast-2",      // AWS 리전 (예: ap-northeast-2)
                genFile.getFileDir(),
                genFile.getOriginFileName());

        return GenFileResponseDTO.builder()
                .id(genFile.getId())
                .url(fileUrl)
                .originFileName(genFile.getOriginFileName())
                .fileSize(genFile.getFileSize())
                .fileExt(genFile.getFileExt())
                .build();
    }
}