package org.example.flowday.standard.util;


import org.apache.tika.Tika;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Util {
    public static class date {

        public static String getCurrentDateFormatted(String pattern) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }
    }

    public static class file {

        public static String getExt(String filename) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                    .orElse("");
        }

        public static String downloadImg(String url, String filePath) {
            // 디렉토리 생성
            File parentDir = new File(filePath).getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 이미지 다운로드
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("이미지 다운로드 실패: HTTP " + response.getStatusCode());
            }

            byte[] imageBytes = response.getBody();

            // 파일 저장
            try {
                Files.write(Paths.get(filePath), imageBytes);
            } catch (IOException e) {
                throw new RuntimeException("이미지 파일 저장에 실패했습니다: " + e.getMessage(), e);
            }

            // MIME 타입 확인 및 확장자 추출
            String mimeType;
            try {
                mimeType = new Tika().detect(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException("MIME 타입 감지에 실패했습니다: " + e.getMessage(), e);
            }

            // 허용된 이미지 MIME 타입 목록
            List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

            if (!allowedMimeTypes.contains(mimeType)) {
                throw new IllegalArgumentException("허용되지 않는 MIME 타입입니다: " + mimeType);
            }

            String ext = mimeType.replaceAll("image/", "");
            ext = ext.replaceAll("jpeg", "jpg");

            String newFilePath = filePath + "." + ext;

            // 파일 이름 변경
            try {
                Files.move(Paths.get(filePath), Paths.get(newFilePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("파일 이름 변경에 실패했습니다: " + e.getMessage(), e);
            }

            return newFilePath;
        }




    }
}