package org.example.flowday.global.fileupload.mapper;

import org.example.flowday.domain.post.post.dto.GenFileResponseDTO;
import org.example.flowday.global.fileupload.entity.GenFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GenFileMapper {
    public static GenFileResponseDTO toResponseDTO(GenFile genFile) {
        // 파일 경로와 파일 이름을 URL 인코딩
        String encodedFileDir = encodePath(genFile.getFileDir());
        String encodedS3FileName = encodePath(genFile.getS3FileName());

        // S3에 저장된 파일의 URL을 사용하여 파일 URL 생성
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s/%s",
                "flowday", // 버킷 이름
                "ap-northeast-2", // AWS 리전
                encodedFileDir,
                encodedS3FileName); // 실제 S3에 저장된 파일 이름 (UUID 포함된 이름)

        return GenFileResponseDTO.builder()
                .id(genFile.getId())
                .url(fileUrl)
                .originFileName(genFile.getOriginFileName())
                .fileSize(genFile.getFileSize())
                .fileExt(genFile.getFileExt())
                .build();
    }

    // 경로의 각 부분을 인코딩하는 메서드
    private static String encodePath(String path) {
        return Arrays.stream(path.split("/"))
                .map(segment -> {
                    try {
                        return URLEncoder.encode(segment, StandardCharsets.UTF_8.toString())
                                .replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("인코딩 실패", e);
                    }
                })
                .collect(Collectors.joining("/"));
    }
}