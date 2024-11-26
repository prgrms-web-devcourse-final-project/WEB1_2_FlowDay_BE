package org.example.flowday.global.fileupload.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.global.config.AppConfig;
import org.example.flowday.global.fileupload.entity.GenFile;
import org.example.flowday.global.fileupload.repository.GenFileRepository;
import org.example.flowday.standard.util.Util;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenFileService {
    private final GenFileRepository genFileRepository;


    private String getCurrentDirName(String relTypeCode) {
        return relTypeCode + "/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");
    }


    public List<GenFile> saveFiles(Post post, List<MultipartFile> images) {
        String relTypeCode = "post";
        long relId = post.getId();

        List<GenFile> genfiles = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                continue;
            }

            String typeCode = "common";
            String type2Code = "inBody";
            String originFileName = image.getOriginalFilename();
            String fileExt = Util.file.getExt(originFileName);
            int fileNo = 1;
            int fileSize = (int) image.getSize();

            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
            String fileDir = "post/" + currentDate;


            GenFile genFile = GenFile
                    .builder()
                    .relTypeCode(relTypeCode)
                    .relId(relId)
                    .typeCode(typeCode)
                    .type2Code(type2Code)
                    .fileNo(fileNo)
                    .fileSize(fileSize)
                    .fileDir(fileDir)
                    .fileExt(fileExt)
                    .originFileName(originFileName)
                    .build();

            genFileRepository.save(genFile);

            String filePath = AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + genFile.getFileName();

            File file = new File(filePath);

            file.getParentFile().mkdirs();

            try {
                image.transferTo(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            genfiles.add(genFile);


        }

        return genfiles;

    }

    public void addGenFileByUrl(String relTypeCode, Long relId, String typeCode, String type2Code, int fileNo, String url) {
        String fileDir = getCurrentDirName(relTypeCode);

        String downFilePath = Util.file.downloadImg(url, AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + UUID.randomUUID());

        File downloadedFile = new File(downFilePath);

        String originFileName = downloadedFile.getName();
        String fileExt = Util.file.getExt(originFileName);
        int fileSize = 0;
        try {
            fileSize = (int) Files.size(Paths.get(downFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GenFile genFile = GenFile
                .builder()
                .relTypeCode(relTypeCode)
                .relId(relId)
                .typeCode(typeCode)
                .type2Code(type2Code)
                .fileNo(fileNo)
                .fileSize(fileSize)
                .fileDir(fileDir)
                .fileExt(fileExt)
                .originFileName(originFileName)
                .build();

        genFileRepository.save(genFile);

        String filePath = AppConfig.GET_FILE_DIR_PATH + "/" + fileDir + "/" + genFile.getFileName();

        File file = new File(filePath);

        file.getParentFile().mkdirs();

        downloadedFile.renameTo(file);
    }

    public Map<String , GenFile> getRelGenFileMap(Post post) {
        List<GenFile> genFiles = genFileRepository.findByRelTypeCodeAndRelId("post", post.getId());

        return genFiles
                .stream()
                .collect(Collectors.toMap(
                        genFile -> genFile.getTypeCode() + "__" + genFile.getType2Code() + "__" + genFile.getFileNo(),
                        genFile -> genFile
                ));
    }
}

