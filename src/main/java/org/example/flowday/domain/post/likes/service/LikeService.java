package org.example.flowday.domain.post.likes.service;

import lombok.RequiredArgsConstructor;

import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.likes.dto.LikesDTO;
import org.example.flowday.domain.post.likes.entity.Likes;
import org.example.flowday.domain.post.likes.repository.LikeRepository;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.exception.PostException;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    // 좋아요 생성
    @Transactional
    public LikesDTO.LikeResponseDTO addLike(LikesDTO.LikeRequestDTO likeRequestDTO , Long userId) {
        Post post = postRepository.findById(likeRequestDTO.getPostId()).orElseThrow(PostException.POST_NOT_FOUND::get);

        Optional<Likes> opLike = likeRepository.findByPostIdAndMemberId(likeRequestDTO.getPostId(), userId);
        if (opLike.isPresent()) {
            throw PostException.POST_IS_LIKE.get();
        }

        post.increaseLike();
        Likes like = Likes.builder()
                .memberId(userId)
                .postId(likeRequestDTO.getPostId())
                .build();

        Likes savedLike = likeRepository.save(like);
        return new LikesDTO.LikeResponseDTO(
                savedLike.getId(),
                savedLike.getMemberId(),
                savedLike.getPostId(),
                "좋아요를 눌렀습니다"
        );

    }
    // 좋아요 삭제
    @Transactional
    public void removeLike(LikesDTO.LikeRequestDTO likeRequestDTO, Long userId) {
        Post post = postRepository.findById(likeRequestDTO.getPostId()).orElseThrow(PostException.POST_NOT_FOUND::get);
        Optional<Likes> opLike = likeRepository.findByPostIdAndMemberId(likeRequestDTO.getPostId(), userId);
        if (opLike.isPresent()) {
            post.decreaseLike();
            likeRepository.delete(opLike.get());
        }

    }

}
