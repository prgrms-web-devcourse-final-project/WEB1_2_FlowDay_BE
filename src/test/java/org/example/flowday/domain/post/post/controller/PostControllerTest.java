package org.example.flowday.domain.post.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.example.flowday.domain.post.post.service.PostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Course course;
    private Post post;
    private PostRequestDTO postRequestDTO;
    private PostResponseDTO postResponseDTO;

    @BeforeAll
    void setUp() {
        member = Member.builder()
                .name("tester")
                .loginId("testId")
                .pw("password")
                .build();

        memberRepository.save(member);

        course = Course.builder()
                .member(member)
                .title("코스 이름")
                .status(Status.COUPLE)
                .date(LocalDate.now())
                .color("blue")
                .spots(List.of(Spot.builder()
                        .placeId("ChIJgUbEo1")
                        .name("장소 이름1")
                        .city("서울")
                        .sequence(1)
                        .build()))
                .createdAt(LocalDateTime.now())
                .build();

        courseRepository.save(course);

        post = Post.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .status(org.example.flowday.domain.post.post.entity.Status.PUBLIC)
                .writer(member)
                .build();

        postRepository.save(post);

        postRequestDTO = PostRequestDTO.builder()
                .title("제목")
                .contents("내용")
                .region("서울")
                .season("겨울")
                .status(org.example.flowday.domain.post.post.entity.Status.PUBLIC)
                .courseId(1L)
                .build();

        postResponseDTO = postService.createPost(postRequestDTO, member.getId());
    }

    @DisplayName("게시글 생성 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void createPost() throws Exception {
        String postJson = objectMapper.writeValueAsString(postRequestDTO);
        MockMultipartFile jsonFile = new MockMultipartFile("postRequestDto", "postRequestDto", "application/json", postJson.getBytes());

        mockMvc.perform(multipart("/api/v1/posts")
                        .file(jsonFile)
                        .param("title", "제목")
                        .param("contents", "내용")
                        .param("region", "서울")
                        .param("season", "겨울")
                        .param("status", "PUBLIC")
                        .param("courseId", String.valueOf(course.getId()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().string("게시글이 작성되었습니다"));
    }

    @DisplayName("게시글 디테일 - ID 조회 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void getPostById() throws Exception {
        mockMvc.perform(get("/api/v1/posts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("제목"));
    }

    @DisplayName("모든 게시글 조회 최신순 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void getAllPosts() throws Exception {
       mockMvc.perform(get("/api/v1/posts/all/latest")
                        .param("page", String.valueOf(1))
                        .param("pageSize", String.valueOf(10)))
                .andExpect(status().isOk());
    }

    @DisplayName("커플 게시글 리스트 조회 테스트")
    @Test
    @WithUserDetails(value = "testId", userDetailsServiceBeanName = "securityUserService")
    void getAllCouplePosts() throws Exception  {
        mockMvc.perform(get("/api/v1/posts/all/couple")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

    }

    @Test
    void getAllPrivatePosts() {
    }
}