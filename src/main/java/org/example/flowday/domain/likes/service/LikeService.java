package org.example.flowday.domain.likes.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.likes.dto.LikeRequestDTO;
import org.example.flowday.domain.likes.dto.LikeResponseDTO;
import org.example.flowday.domain.likes.entity.LikeEntity;
import org.example.flowday.domain.likes.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;

    // 좋아요 생성
    @Transactional
    public LikeResponseDTO addLike(LikeRequestDTO likeRequestDTO) {
        if (likeRepository.existsLike(likeRequestDTO.getUserId(), likeRequestDTO.getPostId())) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시물입니다.");
        }

        LikeEntity like = LikeEntity.builder()
                .userId(likeRequestDTO.getUserId())
                .postId(likeRequestDTO.getPostId())
                .createdAt(LocalDateTime.now())
                .build();

        LikeEntity savedLike = likeRepository.save(like);
        return LikeResponseDTO.builder()
                .id(savedLike.getId())
                .userId(savedLike.getUserId())
                .postId(savedLike.getPostId())
                .createdAt(savedLike.getCreatedAt())
                .build();
    }

    // 좋아요 삭제
    @Transactional
    public void removeLike(Long userId, Long postId) {
        if (!likeRepository.existsLike(userId, postId)) {
            throw new IllegalArgumentException("좋아요가 존재하지 않습니다.");
        }
        likeRepository.deleteLike(userId, postId);
    }

    // 특정 좋아요 조회
    public Optional<LikeResponseDTO> getLikeById(Long id) {
        return likeRepository.findById(id)
                .map(like -> LikeResponseDTO.builder()
                        .id(like.getId())
                        .userId(like.getUserId())
                        .postId(like.getPostId())
                        .createdAt(like.getCreatedAt())
                        .build());
    }
}
