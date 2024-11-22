package org.example.flowday.domain.post.comment.likecomment.controller;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.comment.likecomment.entity.LikeReply;
import org.example.flowday.domain.post.comment.likecomment.repository.LikeReplyRepository;
import org.example.flowday.domain.post.comment.likecomment.service.LikeReplyService;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
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

    private Member testMember;
    private Member otherMember;
    private Post testPost;
    private Reply testReply;
    @Autowired
    private LikeReplyService likeReplyService;

    @BeforeEach
    void setUp() {
        // 테스트에 필요한 회원 생성
        testMember = Member.builder()
                .name("테스트유저")
                .email("testuser@example.com")
                .pw("password")
                .build();
        memberRepository.save(testMember);

        otherMember = Member.builder()
                .name("다른유저")
                .email("otheruser@example.com")
                .pw("password")
                .build();
        memberRepository.save(otherMember);

        // 테스트에 필요한 게시글 생성
        testPost = Post.builder()
                .title("테스트 게시글")
                .content("게시글 내용")
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
    @DisplayName("POST /api/v1/likes/{replyId} - 좋아요 생성 성공")
    void createLikeReply_Success() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/likes/{replyId}", testReply.getId())
                        .param("memberId", testMember.getId().toString())
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
    @DisplayName("POST /api/v1/likes/{replyId} - 회원 존재하지 않음")
    void createLikeReply_MemberNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/likes/{replyId}", testReply.getId())
                        .param("memberId", "9999") // 존재하지 않는 회원 ID
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("해당 멤버가 없습니다")));

        // 좋아요가 생성되지 않았는지 확인
        boolean exists = likeReplyRepository.findByReplyAndMember(testReply, testMember).isPresent();
        assertThat(exists).isFalse();

        // 댓글의 좋아요 수가 변하지 않았는지 확인
        Reply updatedReply = replyRepository.findById(testReply.getId()).orElseThrow();
        assertThat(updatedReply.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("POST /api/v1/likes/{replyId} - 댓글 존재하지 않음")
    void createLikeReply_ReplyNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/likes/{replyId}", 9999L) // 존재하지 않는 댓글 ID
                        .param("memberId", testMember.getId().toString())
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
    @DisplayName("POST /api/v1/likes/{replyId} - 이미 좋아요를 누른 경우")
    void createLikeReply_AlreadyLiked() throws Exception {
        // 좋아요 생성
        likeReplyService.saveLikeReply(testMember.getId() , testReply.getId());

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/likes/{replyId}", testReply.getId())
                        .param("memberId", testMember.getId().toString())
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
    @DisplayName("DELETE /api/v1/likes/{replyId} - 좋아요 삭제 성공")
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
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/likes/{replyId}", testReply.getId())
                        .param("memberId", testMember.getId().toString())
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
    @DisplayName("DELETE /api/v1/likes/{replyId} - 회원 존재하지 않음")
    void deleteLikeReply_MemberNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/likes/{replyId}", testReply.getId())
                        .param("memberId", "9999") // 존재하지 않는 회원 ID
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("해당 멤버가 없습니다")));

        // 댓글의 좋아요 수가 변하지 않았는지 확인
        Reply updatedReply = replyRepository.findById(testReply.getId()).orElseThrow();
        assertThat(updatedReply.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("DELETE /api/v1/likes/{replyId} - 댓글 존재하지 않음")
    void deleteLikeReply_ReplyNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/likes/{replyId}", 9999L) // 존재하지 않는 댓글 ID
                        .param("memberId", testMember.getId().toString())
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
    @DisplayName("DELETE /api/v1/likes/{replyId} - 좋아요 존재하지 않음")
    void deleteLikeReply_LikeNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/likes/{replyId}", testReply.getId())
                        .param("memberId", testMember.getId().toString())
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