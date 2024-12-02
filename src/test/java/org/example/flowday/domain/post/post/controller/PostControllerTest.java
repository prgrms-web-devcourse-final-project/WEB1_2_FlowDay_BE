package org.example.flowday.domain.post.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.likes.entity.Likes;
import org.example.flowday.domain.post.likes.repository.LikeRepository;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.entity.Status;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember1;
    private Member testMember2;
    private Post testPost1;
    private Post testPost2;
    private Post testPost3;


    @AfterAll
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @BeforeAll
    void setUp() {
        // 테스트 멤버 생성
        testMember1 = Member.builder()
                .name("테스트유저1")
                .loginId("testuser1@example.com")
                .pw("password")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(testMember1);


        testMember2 = Member.builder()
                .name("테스트유저2")
                .loginId("testuser2@example.com")
                .pw("password")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(testMember2);

        // 멤버 간 파트너 설정 (커플 관계)
        testMember1.setPartnerId(testMember2.getId());
        testMember2.setPartnerId(testMember1.getId());
        memberRepository.save(testMember1);
        memberRepository.save(testMember2);



        // 테스트 게시글 생성
        testPost1 = Post.builder()
                .title("테스트 게시글")
                .contents("테스트 내용")
                .writer(testMember1)
                .status(Status.PUBLIC)
                .build();
        postRepository.save(testPost1);

        testPost2 = Post.builder()
                .title("테스트 게시글2")
                .contents("테스트 내용2")
                .writer(testMember2)
                .status(Status.PRIVATE)
                .build();
        postRepository.save(testPost2);

        testPost3 = Post.builder()
                .title("테스트 게시글2")
                .contents("테스트 내용2")
                .writer(testMember1)
                .status(Status.COUPLE)
                .build();
        postRepository.save(testPost3);




        // 좋아요 생성
        Likes like = Likes.builder()
                .memberId(testMember1.getId())
                .postId(testPost2.getId())
                .build();
        likeRepository.save(like);

        // 댓글 생성
        Reply reply = Reply.builder()
                .content("테스트 댓글")
                .member(testMember1)
                .post(testPost2)
                .build();
        replyRepository.save(reply);
    }

    @Test
    @DisplayName("POST /api/v1/posts - 게시글 생성 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void createPost_Success() throws Exception {
        // 요청 데이터 준비
        MockMultipartHttpServletRequestBuilder builder = multipart("/api/v1/posts");
        builder.with(request -> {
            request.setMethod("POST");
            return request;
        });

        builder.param("title", "새로운 게시글");
        builder.param("contents", "새로운 게시글 내용");
        builder.param("region", "서울");
        builder.param("season", "봄");
        builder.param("tags", "테스트,게시글");
        builder.param("status", "PUBLIC");

        // 요청 수행
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("게시글이 작성되었습니다"));
    }


    @Test
    @DisplayName("GET /api/v1/posts/{id} - 게시글 조회 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void getPostById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/posts/{id}", testPost1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testPost1.getId().intValue())))
                .andExpect(jsonPath("$.title", is(testPost1.getTitle())))
                .andExpect(jsonPath("$.contents", is(testPost1.getContents())))
                .andExpect(jsonPath("$.nickName", is(testMember1.getName())));
    }

    @Test
    @DisplayName("GET /api/v1/posts/all/latest - 최신 게시글 목록 조회 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void getAllPosts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/posts/all/latest")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].id", is(testPost1.getId().intValue())));
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{id} - 게시글 수정 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void updatePost_Success() throws Exception {
        // 수정할 데이터 준비
        MockMultipartHttpServletRequestBuilder builder = multipart("/api/v1/posts/{id}", testPost1.getId());
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        builder.param("title", "수정된 게시글");
        builder.param("contents", "수정된 내용");
        builder.param("region", "부산");
        builder.param("season", "여름");
        builder.param("tags", "수정,테스트");
        builder.param("status", "PRIVATE");

        // 요청 수행
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("수정된 게시글")))
                .andExpect(jsonPath("$.contents", is("수정된 내용")));
    }


    @Test
    @DisplayName("DELETE /api/v1/posts/{id} - 게시글 삭제 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void deletePost_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/{id}", testPost1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("게시글이 삭제되었습니다"));
    }

    @Test
    @DisplayName("GET /api/v1/posts/all - 내 게시글 목록 조회 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void getAllMyPosts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/posts/all")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nickName", is("테스트유저1")));

    }
    @Test
    @DisplayName("GET /api/v1/posts/all/mostLike - 모든 게시글 인기순 조회 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void getAllPostsMostLike_Success() throws Exception {
        mockMvc.perform(get("/api/v1/posts/all/mostLike")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("GET /api/v1/posts/all/couple - 커플 게시글 조회 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void getAllCouplePosts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/posts/all/couple")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$.content[0].status", is("COUPLE")));

    }

    @Test
    @DisplayName("GET /api/v1/posts/all/private - Private 게시글 조회 성공")
    @WithUserDetails(value = "testuser2@example.com", userDetailsServiceBeanName = "securityUserService")
    void getAllPrivatePosts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/posts/all/private")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status", is("PRIVATE")));
    }


    @Test
    @DisplayName("GET /api/v1/posts/all/likes - 내가 좋아요 누른 게시글 조회 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void getAllMyLikesPosts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/posts/all/likes")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(testPost2.getId().intValue())));
    }

    @Test
    @DisplayName("GET /api/v1/posts/all/reply - 내가 댓글 단 게시글 조회 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void getAllMyReplyPosts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/posts/all/reply")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(testPost2.getId().intValue())));
    }



}
