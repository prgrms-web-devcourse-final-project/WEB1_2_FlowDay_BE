package org.example.flowday.global.fileupload.repository;

import org.example.flowday.global.fileupload.entity.GenFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenFileRepository extends JpaRepository<GenFile, Long> {
    List<GenFile> findByRelTypeCodeAndRelId(String post, Long id);
}
