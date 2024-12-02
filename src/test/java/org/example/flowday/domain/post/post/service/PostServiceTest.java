package org.example.flowday.domain.post.post.service;

import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.post.dto.PostRequestDTO;
import org.example.flowday.domain.post.post.dto.PostResponseDTO;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.mapper.PostMapper;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.example.flowday.global.fileupload.service.GenFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private GenFileService genFileService;

    @Mock
    private PostMapper postMapper;

    private Member member;
    private Member partner;
    private Course course;
    private Post post;
    private Post post2;
    private Post post3;
    private Post post4;

    @BeforeEach
    void PostServiceTest() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .id(1L)
                .loginId("testId")
                .pw("testPw")
                .email("test@test.com")
                .name("tester")
                .refreshToken("refresh_token_value")
                .role(Role.ROLE_USER)
                .partnerId(2L)
                .build();

        partner = Member.builder()
                .id(2L)
                .loginId("testId2")
                .pw("testPw2")
                .email("test@test.com")
                .name("tester2")
                .refreshToken("refresh_token_value")
                .role(Role.ROLE_USER)
                .partnerId(2L)
                .build();

        course = Course.builder()
                .id(1L)
                .member(member)
                .title("코스 이름")
                .status(Status.PRIVATE)
                .date(LocalDate.now())
                .color("#FFFFFF")
                .spots(List.of(Spot.builder()
                        .id(1L)
                        .placeId("ChIJgUbEo1")
                        .name("장소 이름1")
                        .city("서울")
                        .sequence(1)
                        .build()))
                .createdAt(LocalDateTime.now())
                .build();

        post = Post.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .status(org.example.flowday.domain.post.post.entity.Status.PUBLIC)
                .writer(member)
                .build();

        post2 = Post.builder()
                .id(2L)
                .title("제목2")
                .contents("내용2")
                .status(org.example.flowday.domain.post.post.entity.Status.PUBLIC)
                .writer(member)
                .build();

        post3 = Post.builder()
                .id(3L)
                .title("제목2")
                .contents("내용2")
                .status(org.example.flowday.domain.post.post.entity.Status.COUPLE)
                .writer(partner)
                .build();

        post4 = Post.builder()
                .id(4L)
                .title("제목2")
                .contents("내용2")
                .status(org.example.flowday.domain.post.post.entity.Status.PRIVATE)
                .writer(partner)
                .build();
    }

    @DisplayName("게시글 생성 테스트")
    @Test
    void createPost() {
        PostRequestDTO postRequestDTO = PostRequestDTO.builder()
                .title("제목")
                .contents("내용")
                .region("서울")
                .season("겨울")
                .status(org.example.flowday.domain.post.post.entity.Status.PUBLIC)
                .courseId(1L)
                .build();

        PostResponseDTO postResponseDTO = PostResponseDTO.builder()
                .title("제목")
                .contents("내용")
                .region("서울")
                .season("겨울")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(postMapper.toEntity(any(PostRequestDTO.class), any(Member.class), any(Course.class))).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponseDTO(any(Post.class), anyList(), anyList())).thenReturn(postResponseDTO);

        PostResponseDTO responseDTO = postService.createPost(postRequestDTO, 1L);

        assertThat(responseDTO).isNotNull();
        assertEquals("제목", responseDTO.getTitle());
        verify(memberRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postMapper, times(1)).toEntity(any(PostRequestDTO.class), any(Member.class), any(Course.class));
        verify(postMapper, times(1)).toResponseDTO(any(Post.class), anyList(), anyList());
    }

    @DisplayName("게시글 디테일 - ID 조회 테스트")
    @Test
    void getPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toResponseDTO(post, null, new ArrayList<>())).thenReturn(new PostResponseDTO());

        PostResponseDTO responseDTO = postService.getPostById(1L);

        assertNotNull(responseDTO);
        verify(postRepository, times(1)).findById(1L);
    }

    @DisplayName("모든 게시글 조회 최신순 테스트")
    @Test
    void getAllPosts() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        postList.add(post2);
        postList.add(post3);
        Page<Post> posts = new PageImpl<>(postList);

        when(postRepository.searchLatestPost(pageRequest)).thenReturn(posts);

        assertNotNull(posts);
        assertEquals(3, posts.getTotalElements());
    }

    @DisplayName("커플 게시글 리스트 조회 테스트")
    @Test
    void findAllCouplePosts() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        postList.add(post2);
        postList.add(post3);
        postList.add(post4);
        Page<Post> posts = new PageImpl<>(postList);

        when(postRepository.searchCouplePost(pageRequest, member.getId(), member.getPartnerId())).thenReturn(posts);

        assertNotNull(posts);
        assertEquals(4, posts.getTotalElements());
    }

    @Test
    void findAllPrivate() {
    }

    @Test
    void deletePost() {
    }
}