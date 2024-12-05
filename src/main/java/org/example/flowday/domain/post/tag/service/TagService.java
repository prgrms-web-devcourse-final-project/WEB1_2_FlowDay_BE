package org.example.flowday.domain.post.tag.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.tag.dto.TagRequestDTO;
import org.example.flowday.domain.post.tag.dto.TagResponseDTO;
import org.example.flowday.domain.post.tag.entity.PostTag;
import org.example.flowday.domain.post.tag.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;


    @Transactional
    public void createTags(String tags, Post post) {
        if (tags == null || tags.isBlank()) {
            return;
        }

        Set<String> tagSet = Arrays.stream(tags.split("#"))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());

        for (String tagContent : tagSet) {
            PostTag postTag = PostTag.builder()
                    .post(post)
                    .content(tagContent)
                    .member(post.getWriter())
                    .build();
            post.getTags().add(postTag);

        }
    }

    @Transactional
    public void updateTags(String tags, Post post) {
        post.getTags().clear();
        createTags(tags, post);

    }


}