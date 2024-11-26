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
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.mapper.PostMapper;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.example.flowday.global.config.AppConfig;
import org.example.flowday.global.fileupload.entity.GenFile;
import org.example.flowday.global.fileupload.repository.GenFileRepository;
import org.example.flowday.global.fileupload.service.GenFileService;
import org.example.flowday.global.security.util.SecurityUser;
import org.example.flowday.standard.util.Util;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final GenFileService  genFileService;

    // 게시글 생성
    @Transactional
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO , Long userId) {
        Member writer = memberRepository.findById(userId).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);
        Course course = null;
        if (postRequestDTO.getCourseId() != null) {
            course = courseRepository.findById(postRequestDTO.getCourseId()).orElseThrow(CourseException.NOT_FOUND::get);
            List<Spot> spots = course.getSpots();
            List<SpotResDTO> spotResDTOs = new ArrayList<>();
            Post post = postMapper.toEntity(postRequestDTO , writer ,course);
            Post savedPost = postRepository.save(post);

            for (Spot spot : spots) {
                spotResDTOs.add(new SpotResDTO(spot));
            }
            return postMapper.toResponseDTO(savedPost, spotResDTOs);
        }
        Post post = postMapper.toEntity(postRequestDTO , writer ,course);
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDTO(savedPost , null);
    }

    // 게시글 디테일 - ID
    public PostResponseDTO getPostById(Long id) {

        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(" 해당 게시글이 존재하지 않습니다 "));
        Course course = post.getCourse();
        if (course != null) {
            List<Spot> spots = course.getSpots();
            List<SpotResDTO> spotResDTOs = new ArrayList<>();
            for (Spot spot : spots) {
                spotResDTOs.add(new SpotResDTO(spot));
            }

            return postMapper.toResponseDTO(post, spotResDTOs);
        }

        return postMapper.toResponseDTO(post, null);

    }

    // 모든 게시글 조회 최신순
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.searchLatestPost(pageable);

        return posts.map(post -> postMapper.toResponseDTO(post , null));

    }

    // 게시글 수정
//    @Transactional
//    public PostResponseDTO updatePost(Long id, PostRequestDTO updatedPostDTO) {
//        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//        post.setTitle(updatedPostDTO.getTitle());
//        post.setContents(updatedPostDTO.getContents());
//        post.setCity(updatedPostDTO.getCity());
//        post.setCourseId(updatedPostDTO.getCourseId());
//        return postMapper.toResponseDTO(post);
//    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public void addGenFileByUrl(Post post, String typeCode, String type2Code, int fileNo, String url) {
        genFileService.addGenFileByUrl("post", post.getId(), typeCode, type2Code, fileNo, url);
    }

    public Post getPosteById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Map<String,Object> getForPrintArticleById(Long id) {
        Post post = getPosteById(id);
        Map<String, GenFile> genFileMap = genFileService.getRelGenFileMap(post);

        post.getExtra().put("age", 22);
        post.getExtra().put("genFileMap", genFileMap);

        return post.getExtra();
    }

}