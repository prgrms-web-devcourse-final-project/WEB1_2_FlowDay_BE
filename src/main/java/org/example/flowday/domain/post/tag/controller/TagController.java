package org.example.flowday.domain.post.tag.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.example.flowday.domain.post.tag.dto.TagRequestDTO;
//import org.example.flowday.domain.post.tag.dto.TagResponseDTO;
//import org.example.flowday.domain.post.tag.service.TagService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/tags")
//@RequiredArgsConstructor
//public class TagController {
//
//    private final TagService tagService;
//
//    // 태그 생성
//    @PostMapping
//    public ResponseEntity<TagResponseDTO> createTag(@Valid @RequestBody TagRequestDTO tagRequestDTO) {
//        TagResponseDTO createdTag = tagService.createTag(tagRequestDTO);
//        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
//    }
//
//    // 태그 단건 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<TagResponseDTO> getTagById(@PathVariable Long id) {
//        Optional<TagResponseDTO> tag = tagService.getTagById(id);
//        return tag.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    // 모든 태그 조회
//    @GetMapping
//    public ResponseEntity<List<TagResponseDTO>> getAllTags() {
//        List<TagResponseDTO> tags = tagService.getAllTags();
//        return new ResponseEntity<>(tags, HttpStatus.OK);
//    }
//
//    // 태그 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
//        tagService.deleteTag(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//}
