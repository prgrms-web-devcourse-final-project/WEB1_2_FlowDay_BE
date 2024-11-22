package org.example.flowday.domain.post.comment.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.example.flowday.domain.post.comment.comment.dto.ReplyDTO;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;
    private Post testPost;
    private Reply parentReply;

    @BeforeEach
    void setUp() {
        // 테스트에 필요한 회원 생성
        testMember = Member.builder()
                .name("테스트유저")
                .email("test@example.com")
                .pw("password")
                .build();
        memberRepository.save(testMember);

        // 테스트에 필요한 게시글 생성
        testPost = Post.builder()
                .title("테스트 게시글")
                .content("게시글 내용")
                .writer(testMember)
                .build();
        postRepository.save(testPost);

        // 테스트에 필요한 부모 댓글 생성
        parentReply = Reply.builder()
                .content("부모 댓글")
                .member(testMember)
                .post(testPost)
                .build();
        replyRepository.save(parentReply);
    }

    @Test
    @DisplayName("GET /api/v1/replies/{postId} - 성공")
    void getRepliesByPostId_Success() throws Exception {
        // 자식 댓글 생성
        Reply childReply = Reply.builder()
                .content("자식 댓글")
                .member(testMember)
                .post(testPost)
                .parent(parentReply)
                .build();
        replyRepository.save(childReply);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/v1/replies/{postId}", testPost.getId()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // 부모 댓글 하나
                .andExpect(jsonPath("$[0].id", is(parentReply.getId().intValue())))
                .andExpect(jsonPath("$[0].content", is("부모 댓글")))
                .andExpect(jsonPath("$[0].memberName", is("테스트유저")))
                .andExpect(jsonPath("$[0].likeCount", is(0)))
                .andExpect(jsonPath("$[0].children", hasSize(1)))
                .andExpect(jsonPath("$[0].children[0].content", is("자식 댓글")));
    }

    @Test
    @DisplayName("GET /api/v1/replies/{postId} - 게시글에 댓글이 없는 경우")
    void getRepliesByPostId_NoReplies() throws Exception {
        // 새로운 게시글 생성 (댓글 없음)
        Post newPost = Post.builder()
                .title("새 게시글")
                .content("새 게시글 내용")
                .writer(testMember)
                .build();
        postRepository.save(newPost);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/v1/replies/{postId}", newPost.getId()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/replies/{postId} - 존재하지 않는 게시글")
    void getRepliesByPostId_NotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/v1/replies/{postId}", 9999L))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("존재하지 않는 게시글 입니다"))); // 게시글 Exception merge시 문구 수정 요구
    }

    @Test
    @DisplayName("POST /api/v1/replies/{postId} - 성공")
    void createReply_Success() throws Exception {
        // 요청 DTO 생성
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("새 댓글", parentReply.getId());


        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/replies/{postId}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg", is("댓글이 생성되었습니다")))
                .andExpect(jsonPath("$.content", is("새 댓글")))
                .andExpect(jsonPath("$.memberName", is("테스트유저")))
                .andExpect(jsonPath("$.likeCount", is(0)))
                .andExpect(jsonPath("$.parentId", is(parentReply.getId().intValue())))
                .andExpect(jsonPath("$.postId", is(testPost.getId().intValue())))
                .andExpect(jsonPath("$.replyId", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/replies/{postId} - 유효성 검사 실패 (내용 없음)")
    void createReply_ValidationFail_NoContent() throws Exception {
        // 요청 DTO 생성 (내용 없음)
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("",null);


        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/replies/{postId}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글 내용을 작성해주세요")));
    }

    @Test
    @DisplayName("POST /api/v1/replies/{postId} - 존재하지 않는 게시글")
    void createReply_PostNotFound() throws Exception {
        // 요청 DTO 생성
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("댓글 내용",null);


        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/replies/{postId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("존재하지 않는 게시글입니다")));
    }

    @Test
    @DisplayName("PATCH /api/v1/replies/{replyId} - 성공")
    void updateReply_Success() throws Exception {
        // 기존 댓글 생성
        Reply existingReply = Reply.builder()
                .content("기존 댓글")
                .member(testMember)
                .post(testPost)
                .build();
        replyRepository.save(existingReply);

        // 요청 DTO 생성
        ReplyDTO.updateRequest request = new ReplyDTO.updateRequest("수정된 댓글");


        // WHEN
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/replies/{replyId}", existingReply.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("댓글이 수정되었습니다")))
                .andExpect(jsonPath("$.content", is("수정된 댓글")));
    }

    @Test
    @DisplayName("PATCH /api/v1/replies/{replyId} - 유효성 검사 실패 (내용 없음)")
    void updateReply_ValidationFail_NoContent() throws Exception {
        // 기존 댓글 생성
        Reply existingReply = Reply.builder()
                .content("기존 댓글")
                .member(testMember)
                .post(testPost)
                .build();
        replyRepository.save(existingReply);

        // 요청 DTO 생성 (내용 없음)
        ReplyDTO.updateRequest request = new ReplyDTO.updateRequest("");

        // WHEN
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/replies/{replyId}", existingReply.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("수정 할 댓글 내용을 작성해주세요")));
    }

    @Test
    @DisplayName("PATCH /api/v1/replies/{replyId} - 존재하지 않는 댓글")
    void updateReply_ReplyNotFound() throws Exception {
        // 요청 DTO 생성
        ReplyDTO.updateRequest request = new ReplyDTO.updateRequest("수정된 댓글");

        // WHEN
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/replies/{replyId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글을 찾을 수 없습니다")));
    }

    @Test
    @DisplayName("PATCH /api/v1/replies/{replyId} - 권한 없음")
    void updateReply_Unauthorized() throws Exception {
        // 다른 회원 생성
        Member otherMember = Member.builder()
                .name("다른유저")
                .email("other@example.com")
                .pw("password")
                .build();
        memberRepository.save(otherMember);

        // 기존 댓글 생성
        Reply existingReply = Reply.builder()
                .content("기존 댓글")
                .member(testMember)
                .post(testPost)
                .build();
        replyRepository.save(existingReply);

        // 요청 DTO 생성
        ReplyDTO.updateRequest request = new ReplyDTO.updateRequest("수정된 댓글");

        // WHEN
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/replies/{replyId}", existingReply.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .param("memberId", otherMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("댓글 작성자만 수정,삭제 할 수 있습니다")));
    }

    @Test
    @DisplayName("DELETE /api/v1/replies/{replyId} - 자식 댓글 삭제 성공")
    void deleteChildReply_Success() throws Exception {
        // 기존 댓글 생성
        Reply existingReply = Reply.builder()
                .content("삭제할 자식 댓글")
                .member(testMember)
                .post(testPost)
                .parent(parentReply)
                .build();
        replyRepository.save(existingReply);

        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/replies/{replyId}", existingReply.getId())
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("자식 댓글 삭제 완료")))
                .andExpect(jsonPath("$.content", is("댓글이 삭제되었습니다")));

        // 데이터베이스에서 삭제 확인
        boolean exists = replyRepository.existsById(existingReply.getId());
        assert(!exists);
    }

    @Test
    @DisplayName("DELETE /api/v1/replies/{replyId} - 부모 댓글 삭제 성공")
    void deleteParentReply_Success() throws Exception {
        // 기존 댓글 생성
        Reply existingReply = Reply.builder()
                .content("삭제할 자식 댓글")
                .member(testMember)
                .post(testPost)
                .parent(null)
                .build();
        replyRepository.save(existingReply);

        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/replies/{replyId}", existingReply.getId())
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("부모 댓글 삭제 완료")))
                .andExpect(jsonPath("$.content", is("작성자에 의해 댓글이 삭제되었습니다")));

        // 데이터베이스에서 부모 댓글 존재
        boolean exists = replyRepository.existsById(existingReply.getId());
        assert(exists);
    }

    @Test
    @DisplayName("DELETE /api/v1/replies/{replyId} - 존재하지 않는 댓글")
    void deleteReply_ReplyNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/replies/{replyId}", 9999L)
                        .param("memberId", testMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글을 찾을 수 없습니다")));
    }

    @Test
    @DisplayName("DELETE /api/v1/replies/{replyId} - 권한 없음")
    void deleteReply_Unauthorized() throws Exception {
        // 다른 회원 생성
        Member otherMember = Member.builder()
                .name("다른유저")
                .email("other@example.com")
                .pw("password")
                .build();
        memberRepository.save(otherMember);

        // 기존 댓글 생성
        Reply existingReply = Reply.builder()
                .content("삭제할 댓글")
                .member(testMember)
                .post(testPost)
                .build();
        replyRepository.save(existingReply);

        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/replies/{replyId}", existingReply.getId())
                        .param("memberId", otherMember.getId().toString()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("댓글 작성자만 수정,삭제 할 수 있습니다")));
    }
}