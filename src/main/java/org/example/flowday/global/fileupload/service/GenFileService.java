package org.example.flowday.global.fileupload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.global.fileupload.entity.GenFile;
import org.example.flowday.global.fileupload.mapper.GenFileMapper;
import org.example.flowday.global.fileupload.repository.GenFileRepository;
import org.example.flowday.standard.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GenFileService {
    private final GenFileRepository genFileRepository;
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;



    private String getCurrentDirName(String relTypeCode) {
        return relTypeCode + "/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");
    }


    public void saveFiles( List<MultipartFile> images , String relTypeCode , Long relId , String typeCode , String type2Code) {
        int fileNo = 1;

        List<GenFile> genFiles = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                continue;
            }

            String originFileName = image.getOriginalFilename();
            String s3FileName = UUID.randomUUID() + "_" + originFileName;
            String fileExt = Util.file.getExt(originFileName);
            int fileSize = (int) image.getSize();

            // S3 파일 저장 경로 설정
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
            String fileDir = relTypeCode+"/" + currentDate;
            String s3Key = fileDir + "/" + s3FileName;

            // S3에 파일 업로드
            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(image.getSize());
                metadata.setContentType(image.getContentType());

                amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, image.getInputStream(), metadata)
                        .withCannedAcl(com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드에 실패했습니다.", e);
            }

            // 파일 정보 저장
            GenFile genFile = GenFile.builder()
                    .relTypeCode(relTypeCode)
                    .relId(relId)
                    .typeCode(typeCode)
                    .type2Code(type2Code)
                    .fileNo(fileNo++)
                    .fileSize(fileSize)
                    .fileDir(fileDir)
                    .fileExt(fileExt)
                    .originFileName(originFileName)
                    .s3FileName(s3FileName) // 실제 S3 파일 이름 저장
                    .build();

            genFile=save(genFile);
            genFileRepository.save(genFile);
            genFiles.add(genFile);

        }


    }

    //원하는 객체의 List<GenFile> 조회
    public List<GenFile> getFilesByPost(String relTypeCode , Long relId) {
        return genFileRepository.findByRelTypeCodeAndRelId(relTypeCode, relId);
    }


    //원하는 객체의 첫번째 이미지 조회
    public String getFirstImageUrlByObject(String relTypeCode , Long relId) {
        List<GenFile> genFiles = getFilesByPost(relTypeCode, relId);

        if (genFiles == null || genFiles.isEmpty()) {
            return null;
        }

        GenFile firstGenFile = genFiles.get(0);

        return GenFileMapper.toResponseDTO(firstGenFile).getUrl();
    }


    public GenFile save(GenFile genFile) {
        Optional<GenFile> opOldGenFile = genFileRepository.findByRelTypeCodeAndRelIdAndTypeCodeAndType2CodeAndFileNo(genFile.getRelTypeCode(), genFile.getRelId(), genFile.getTypeCode()
                , genFile.getType2Code(), genFile.getFileNo());

        if(opOldGenFile.isPresent()) {
            GenFile oldGenFile = opOldGenFile.get();
            //s3에 업로드 된 이미지 삭제
            deleteFileFromS3(oldGenFile.getFileDir() , oldGenFile.getS3FileName());

            oldGenFile.merge(genFile);

            genFileRepository.save(oldGenFile);

            return oldGenFile;

        }
        genFileRepository.save(genFile);

        return genFile;

    }

    //s3에 업로드된 파일 삭제
    public void deleteFileFromS3(String fileDir, String s3FileName) {

        String s3Key = fileDir + "/" + s3FileName;
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, s3Key));
            System.out.println("파일이 성공적으로 삭제되었습니다: " + s3Key);
        } catch (Exception e) {
            throw new RuntimeException("S3에서 파일 삭제에 실패했습니다: " + s3Key, e);
        }
    }





    //URL의 사진 꺼내오기
//    public void addGenFileByUrl(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo, String url) {
//        String fileDir = getCurrentDirName(relTypeCode);
//
//        String downFilePath = Util.file.downloadImg(url, AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + UUID.randomUUID());
//
//        File downloadedFile = new File(downFilePath);
//
//        String originFileName = downloadedFile.getName();
//        String fileExt = Util.file.getExt(originFileName);
//        int fileSize = 0;
//        try {
//            fileSize = (int) Files.size(Paths.get(downFilePath));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        GenFile genFile = GenFile
//                .builder()
//                .relTypeCode(relTypeCode)
//                .relId(relId)
//                .typeCode(typeCode)
//                .type2Code(type2Code)
//                .fileNo(fileNo)
//                .fileSize(fileSize)
//                .fileDir(fileDir)
//                .fileExt(fileExt)
//                .originFileName(originFileName)
//                .build();
//
//        genFileRepository.save(genFile);
//
//        String filePath = AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + genFile.getFileName();
//
//        File file = new File(filePath);
//
//        file.getParentFile().mkdirs();
//
//        downloadedFile.renameTo(file);
//    }
//
//    public Map<String , GenFile> getRelGenFileMap(Post post) {
//        List<GenFile> genFiles = genFileRepository.findByRelTypeCodeAndRelId("post", post.getId());
//
//        return genFiles
//                .stream()
//                .collect(Collectors.toMap(
//                        genFile -> genFile.getTypeCode() + "__" + genFile.getType2Code() + "__" + genFile.getFileNo(),
//                        genFile -> genFile
//                ));
//    }


}

