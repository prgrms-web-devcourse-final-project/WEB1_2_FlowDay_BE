package org.example.flowday.domain.post.post.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.exception.CourseException;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.likes.repository.LikeRepository;
import org.example.flowday.domain.post.post.dto.GenFileResponseDTO;
import org.example.flowday.domain.post.post.dto.PostBriefResponseDTO;
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.exception.PostException;
import org.example.flowday.domain.post.tag.service.TagService;
import org.example.flowday.global.fileupload.mapper.GenFileMapper;
import org.example.flowday.domain.post.post.mapper.PostMapper;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.example.flowday.global.fileupload.entity.GenFile;
import org.example.flowday.global.fileupload.repository.GenFileRepository;
import org.example.flowday.global.fileupload.service.GenFileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final GenFileService genFileService;
    private final GenFileRepository genFileRepository;
    private final LikeRepository likeRepository;
    private final ReplyRepository replyRepository;
    private final TagService tagService;

    @Transactional
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO, Long userId) {
        Member writer = memberRepository.findById(userId)
                .orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);
        try {
            Course course = null;
            List<SpotResDTO> spotResDTOs = null;

            if (postRequestDTO.getCourseId() != null) {
                course = courseRepository.findById(postRequestDTO.getCourseId())
                        .orElseThrow(CourseException.NOT_FOUND::get);
                List<Spot> spots = course.getSpots();
                spotResDTOs = spots.stream()
                        .map(SpotResDTO::new)
                        .collect(Collectors.toList());
            }

            // 게시글 생성 및 저장
            Post post = postMapper.toEntity(postRequestDTO, writer, course);
            Post savedPost = postRepository.save(post);
            tagService.createTags(postRequestDTO.getTags(), savedPost);

            // 이미지 저장 로직 추가
            List<MultipartFile> images = postRequestDTO.getImages();
            if (images != null && !images.isEmpty()) {
                genFileService.saveFiles(images, "post", savedPost.getId(), "common", "inBody");
            }

            // 이미지 정보를 포함하여 응답 DTO 생성
            List<GenFile> genFiles = genFileService.getFilesByPost("post", savedPost.getId());
            List<GenFileResponseDTO> imageDTOs = genFiles.stream()
                    .map(GenFileMapper::toResponseDTO)
                    .collect(Collectors.toList());

            return postMapper.toResponseDTO(savedPost, spotResDTOs, imageDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            throw PostException.POST_NOT_CREATED.get();
        }
    }


    // 게시글 디테일 - ID
    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostException.POST_NOT_FOUND::get);

        Course course = post.getCourse();
        List<SpotResDTO> spotResDTOs = null;
        if (course != null) {
            List<Spot> spots = course.getSpots();
            spotResDTOs = spots.stream()
                    .map(SpotResDTO::new)
                    .collect(Collectors.toList());
        }

        // 이미지 정보 가져오기
        List<GenFile> genFiles = genFileService.getFilesByPost("post", post.getId());
        List<GenFileResponseDTO> imageDTOs = genFiles.stream()
                .map(GenFileMapper::toResponseDTO)
                .collect(Collectors.toList());

        return postMapper.toResponseDTO(post, spotResDTOs, imageDTOs);
    }

    // 모든 게시글 조회 최신순 - PUBLIC
    public Page<PostBriefResponseDTO> getAllPublicPosts(Pageable pageable) {
        Page<Post> posts = postRepository.searchLatestPost(pageable);


        return posts.map(post -> {
            String imageURL = genFileService.getFirstImageUrlByObject("post", post.getId());
            return new PostBriefResponseDTO(post, imageURL);
        });

    }

    //커플 게시글 리스트 조회
    public Page<PostBriefResponseDTO> findAllCouplePosts(Pageable pageable, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 멤버가 없습니다 "));
        Long partnerId = (member.getPartnerId() != null) ? member.getPartnerId() : null;

        Page<Post> posts = postRepository.searchCouplePost(pageable, userId, partnerId);

        return posts.map(post -> {
            String imageUrl = genFileService.getFirstImageUrlByObject("post", post.getId());
            return new PostBriefResponseDTO(post, imageUrl);
        });

    }

    //내가 작성한 Private 게시글만 보기
    public Page<PostBriefResponseDTO> findAllPrivate(Pageable pageable, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);

        Page<Post> posts = postRepository.searchPrivatePost(pageable, userId);

        return posts.map(post -> {
            String imageUrl = genFileService.getFirstImageUrlByObject("post", post.getId());
            return new PostBriefResponseDTO(post, imageUrl);
        });
    }

    //좋아요가 많은 게시글 조회
    public Page<PostBriefResponseDTO> findAllMostLikePosts(Pageable pageable) {

        Page<Post> posts = postRepository.searchMostLikedPost(pageable);

        return posts.map(post -> {
            String imageUrl = genFileService.getFirstImageUrlByObject("post", post.getId());
            return new PostBriefResponseDTO(post, imageUrl);
        });
    }


    //내가 작성한 게시글 보기
    public Page<PostBriefResponseDTO> findAllMyPosts(Pageable pageable, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 멤버가 없습니다 "));

        Page<Post> posts = postRepository.searchMyPost(pageable, userId);

        return posts.map(post -> {
            String imageUrl = genFileService.getFirstImageUrlByObject("post", post.getId());
            return new PostBriefResponseDTO(post, imageUrl);
        });
    }

    //내가 좋아요 누른 게시글 보기
    public Page<PostBriefResponseDTO> findAllMyLikePosts(Pageable pageable, Long userId) {
        List<Long> postIds = likeRepository.findAllPostIdByMemberId(userId);

        Page<Post> posts = postRepository.searchMyLikePost(pageable, postIds);

        return posts.map(post -> {
            String imageUrl = genFileService.getFirstImageUrlByObject("post", post.getId());
            return new PostBriefResponseDTO(post, imageUrl);
        });
    }

    //내가 댓글 단 게시글 보기
    public Page<PostBriefResponseDTO> findAllMyReplyPosts(Pageable pageable, Long userId) {


        Page<Post> posts = postRepository.searchMyReplyPost(pageable, userId);

        return posts.map(post -> {
            String imageUrl = genFileService.getFirstImageUrlByObject("post", post.getId());
            return new PostBriefResponseDTO(post, imageUrl);
        });
    }


    //게시글 수정
    @Transactional
    public PostResponseDTO updatePost(Long id, PostRequestDTO updatedPostDTO, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 게시글 정보 수정
        post.updatePost(updatedPostDTO);
        tagService.updateTags(updatedPostDTO.getTags(), post);


        Course course = null;
        List<SpotResDTO> spotResDTOs = null;

        if (updatedPostDTO.getCourseId() != null) {
            course = courseRepository.findById(updatedPostDTO.getCourseId())
                    .orElseThrow(CourseException.NOT_FOUND::get);
            List<Spot> spots = course.getSpots();
            spotResDTOs = spots.stream()
                    .map(SpotResDTO::new)
                    .collect(Collectors.toList());
        }

        post.setCourse(course);

        // 기존 이미지 처리
        List<GenFile> existingGenFiles = genFileService.getFilesByPost("post", post.getId());
        List<MultipartFile> newImages = updatedPostDTO.getImages();

        // 기존 이미지 삭제 처리
        if (newImages == null || newImages.isEmpty()) {
            // 새로운 이미지가 없을 경우 기존 모든 이미지 삭제
            for (GenFile genFileToDelete : existingGenFiles) {
                genFileService.deleteFileFromS3(genFileToDelete.getFileDir(), genFileToDelete.getS3FileName());
                genFileRepository.delete(genFileToDelete);
            }
        } else {
            // 새로운 이미지가 있는 경우 기존 이미지와 비교
            int newImageCount = newImages.size();
            int existingImageCount = existingGenFiles.size();

            // 기존 이미지 중 남는 이미지 삭제
            if (existingImageCount > newImageCount) {
                for (int i = newImageCount; i < existingImageCount; i++) {
                    GenFile genFileToDelete = existingGenFiles.get(i);
                    genFileService.deleteFileFromS3(genFileToDelete.getFileDir(), genFileToDelete.getS3FileName());
                    genFileRepository.delete(genFileToDelete);
                }
            }

            // 새 이미지 저장
            genFileService.saveFiles(newImages, "post", post.getId(), "common", "inBody");
        }


        // 최종 이미지 정보 수집 후 DTO 변환
        List<GenFile> updatedGenFiles = genFileService.getFilesByPost("post", post.getId());
        List<GenFileResponseDTO> imageDTOs = updatedGenFiles.stream()
                .map(GenFileMapper::toResponseDTO)
                .collect(Collectors.toList());

        return postMapper.toResponseDTO(post, spotResDTOs, imageDTOs);
    }


    // 게시글 삭제
    @Transactional
    public void deletePost(Long id, Long userId) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("post is not exist"));
        if (userId != post.getWriter().getId()) {
            throw new RuntimeException("작성자만 게시글을 삭제 할 수 있습니다");
        }
        List<GenFile> genFiles = genFileService.getFilesByPost("post", post.getId());
        for (GenFile genFile : genFiles) {
            genFileService.deleteFileFromS3(genFile.getFileDir(), genFile.getS3FileName());
            genFileRepository.delete(genFile);
        }
        post.remove();
        postRepository.deleteById(id);
    }


    //검색
    public Page<PostBriefResponseDTO> findAllKwPosts(String kw, Pageable pageable) {
        Page<Post> posts = postRepository.searchKwPost(pageable, kw);

        return posts.map(post -> {
            String imageUrl = genFileService.getFirstImageUrlByObject("post", post.getId());
            return new PostBriefResponseDTO(post, imageUrl);
        });

    }
}