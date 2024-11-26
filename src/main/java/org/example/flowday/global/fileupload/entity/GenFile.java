package org.example.flowday.global.fileupload.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GenFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private String relTypeCode;     // entity type
    private long relId;             // entity id
    private String typeCode;        // 파일의 주요 타입
    private String type2Code;       // 파일의 세부 타입
    private int fileSize;            // 파일 크기
    private int fileNo;             // 파일의 각 번호
    private String fileExt;         // 파일 확장자
    private String fileDir;         // 파일 경로
    private String originFileName;  // 원본 파일 이름



    public String getFileName() {
        return getId() + "." + getFileExt();
    }

    public String getUrl() {
        return "/gen/" + getFileDir() + "/" + getFileName();
    }


}
