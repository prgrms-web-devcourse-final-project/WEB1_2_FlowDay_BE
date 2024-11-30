package org.example.flowday.domain.post.comment.likecomment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.comment.likecomment.entity.LikeReply;
import org.example.flowday.domain.post.comment.likecomment.repository.LikeReplyRepository;
import org.example.flowday.domain.post.comment.likecomment.service.LikeReplyService;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LikeReplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LikeReplyRepository likeReplyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private LikeReplyService likeReplyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;
    private Post testPost;
    private Reply testReply;


    @AfterAll
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @BeforeAll
    void setUp() {
        // 테스트에 필요한 회원 생성
        testMember = Member.builder()
                .name("테스트유저")
                .loginId("testuser1@example.com")
                .email("testuser@example.com")
                .pw("password")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(testMember);

        // 테스트에 필요한 게시글 생성
        testPost = Post.builder()
                .title("테스트 게시글")
                .contents("게시글 내용")
                .writer(testMember)
                .build();
        postRepository.save(testPost);

        // 테스트에 필요한 댓글 생성
        testReply = Reply.builder()
                .content("테스트 댓글")
                .member(testMember)
                .post(testPost)
                .build();
        replyRepository.save(testReply);
    }

    @Test
    @DisplayName("POST /api/v1/likes/replies/{replyId} - 좋아요 생성 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void createLikeReply_Success() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/likes/replies/{replyId}", testReply.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("좋아요가 생성되었습니다"));

        // 데이터베이스에 좋아요가 저장되었는지 확인
        boolean exists = likeReplyRepository.findByReplyAndMember(testReply, testMember).isPresent();
        assertThat(exists).isTrue();

        // 댓글의 좋아요 수가 증가했는지 확인
        Reply updatedReply = replyRepository.findById(testReply.getId()).orElseThrow();
        assertThat(updatedReply.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/likes/replies/{replyId} - 댓글 존재하지 않음")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void createLikeReply_ReplyNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/likes/replies/{replyId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글을 찾을 수 없습니다")));

        // 좋아요가 생성되지 않았는지 확인
        boolean exists = likeReplyRepository.findByReplyAndMember(testReply, testMember).isPresent();
        assertThat(exists).isFalse();

        // 댓글의 좋아요 수가 변하지 않았는지 확인
        Reply updatedReply = replyRepository.findById(testReply.getId()).orElseThrow();
        assertThat(updatedReply.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("POST /api/v1/likes/replies/{replyId} - 이미 좋아요를 누른 경우")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void createLikeReply_AlreadyLiked() throws Exception {
        // 좋아요 생성
        likeReplyService.saveLikeReply(testMember.getId(), testReply.getId());

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/likes/replies/{replyId}", testReply.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글에 이미 좋아요를 눌렀습니다")));

        // 댓글의 좋아요 수가 변하지 않았는지 확인
        Reply updatedReply = replyRepository.findById(testReply.getId()).orElseThrow();
        assertThat(updatedReply.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("DELETE /api/v1/likes/replies/{replyId} - 좋아요 삭제 성공")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void deleteLikeReply_Success() throws Exception {
        // 사전에 좋아요 생성
        LikeReply existingLike = LikeReply.builder()
                .member(testMember)
                .reply(testReply)
                .build();
        likeReplyRepository.save(existingLike);

        // 댓글의 좋아요 수 증가
        testReply.increaseLikeCount();
        replyRepository.save(testReply);

        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/likes/replies/{replyId}", testReply.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string("좋아요가 삭제되었습니다"));

        // 데이터베이스에서 좋아요가 삭제되었는지 확인
        boolean exists = likeReplyRepository.findByReplyAndMember(testReply, testMember).isPresent();
        assertThat(exists).isFalse();

        // 댓글의 좋아요 수가 감소했는지 확인
        Reply updatedReply = replyRepository.findById(testReply.getId()).orElseThrow();
        assertThat(updatedReply.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("DELETE /api/v1/likes/replies/{replyId} - 댓글 존재하지 않음")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void deleteLikeReply_ReplyNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/likes/replies/{replyId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글을 찾을 수 없습니다")));

        // 댓글의 좋아요 수가 변하지 않았는지 확인
        Reply updatedReply = replyRepository.findById(testReply.getId()).orElseThrow();
        assertThat(updatedReply.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("DELETE /api/v1/likes/replies/{replyId} - 좋아요 존재하지 않음")
    @WithUserDetails(value = "testuser1@example.com", userDetailsServiceBeanName = "securityUserService")
    void deleteLikeReply_LikeNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/likes/replies/{replyId}", testReply.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("좋아요가 존재하지 않습니다")));

        // 댓글의 좋아요 수가 변하지 않았는지 확인
        Reply updatedReply = replyRepository.findById(testReply.getId()).orElseThrow();
        assertThat(updatedReply.getLikeCount()).isEqualTo(0);
    }
}
