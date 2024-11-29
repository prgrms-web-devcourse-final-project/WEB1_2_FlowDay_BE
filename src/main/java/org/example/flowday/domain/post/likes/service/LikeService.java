package org.example.flowday.domain.post.likes.service;

import lombok.RequiredArgsConstructor;

import org.example.flowday.domain.post.likes.dto.LikesDTO;
import org.example.flowday.domain.post.likes.entity.Likes;
import org.example.flowday.domain.post.likes.repository.LikeRepository;
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
    public LikesDTO.LikeResponseDTO addLike(LikesDTO.LikeRequestDTO likeRequestDTO , Long userId) {

        Likes like = Likes.builder()
                .memberId(userId)
                .postId(likeRequestDTO.getPostId())
                .build();

        Likes savedLike = likeRepository.save(like);
        return new LikesDTO.LikeResponseDTO(
                savedLike.getId(),
                savedLike.getMemberId(),
                savedLike.getPostId());

    }
    // 좋아요 삭제
    @Transactional
    public void removeLike(LikesDTO.LikeRequestDTO likeRequestDTO, Long userId) throws IllegalArgumentException {
        if (!likeRepository.existsByMemberIdAndPostId(userId, likeRequestDTO.getPostId())) {
            throw new IllegalArgumentException("좋아요가 존재하지 않습니다.");
        }
        likeRepository.deleteByMemberIdAndPostId(userId, likeRequestDTO.getPostId());
    }

}
