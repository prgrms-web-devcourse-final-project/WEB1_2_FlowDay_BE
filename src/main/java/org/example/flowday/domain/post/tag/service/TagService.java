package org.example.flowday.domain.post.tag.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.tag.dto.TagRequestDTO;
import org.example.flowday.domain.post.tag.dto.TagResponseDTO;
import org.example.flowday.domain.post.tag.entity.Tag;
import org.example.flowday.domain.post.tag.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    // 태그 생성
    @Transactional
    public TagResponseDTO createTag(TagRequestDTO tagRequestDTO) {
        if (tagRepository.existsByName(tagRequestDTO.getName())) {
            throw new IllegalArgumentException("이미 존재하는 태그입니다.");
        }
        Tag tag = Tag.builder().name(tagRequestDTO.getName()).build();
        Tag savedTag = tagRepository.save(tag);
        return TagResponseDTO.builder().id(savedTag.getId()).name(savedTag.getName()).build();
    }

    // 태그 조회 - ID
    public Optional<TagResponseDTO> getTagById(Long id) {
        return tagRepository.findById(id)
                .map(tag -> TagResponseDTO.builder().id(tag.getId()).name(tag.getName()).build());
    }

    // 모든 태그 조회
    public List<TagResponseDTO> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        List<TagResponseDTO> responseDTOList = new ArrayList<>();
        for (Tag tag : tags) {
            responseDTOList.add(TagResponseDTO.builder().id(tag.getId()).name(tag.getName()).build());
        }
        return responseDTOList;
    }

    // 태그 삭제
    @Transactional
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}