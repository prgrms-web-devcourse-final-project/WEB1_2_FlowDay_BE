package org.example.flowday.domain.post.comment.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.dto.ReplyDTO;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.comment.comment.service.ReplyService;
import org.example.flowday.domain.post.post.entity.Post;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    private ReplyService replyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;
    private Member otherMember;
    private Post testPost;
    private Reply parentReply;


    @AfterAll
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @BeforeAll
    void setUp() {
        // 테스트에 필요한 회원 생성 (테스트 유저)
        testMember = Member.builder()
                .name("테스트유저")
                .loginId("testuser@example.com")
                .pw("password")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(testMember);

        // 테스트에 필요한 다른 회원 생성 (권한 없음)
        otherMember = Member.builder()
                .name("다른유저")
                .loginId("otheruser@example.com")
                .role(Role.ROLE_USER)
                .pw("password")
                .build();
        memberRepository.save(otherMember);

        // 테스트에 필요한 게시글 생성
        testPost = Post.builder()
                .title("테스트 게시글")
                .contents("게시글 내용")
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
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
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
        ResultActions resultActions = mockMvc.perform(get("/api/v1/replies/{postId}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(jsonPath("$[0].children[0].id", is(childReply.getId().intValue())))
                .andExpect(jsonPath("$[0].children[0].content", is("자식 댓글")))
                .andExpect(jsonPath("$[0].children[0].memberName", is("테스트유저")))
                .andExpect(jsonPath("$[0].children[0].likeCount", is(0)));
    }

    @Test
    @DisplayName("GET /api/v1/replies/{postId} - 게시글에 댓글이 없는 경우")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void getRepliesByPostId_NoReplies() throws Exception {
        // 새로운 게시글 생성 (댓글 없음)
        Post newPost = Post.builder()
                .title("새 게시글")
                .contents("새 게시글 내용")
                .writer(testMember)
                .build();
        postRepository.save(newPost);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/v1/replies/{postId}", newPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/replies/{postId} - 존재하지 않는 게시글")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void getRepliesByPostId_NotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/v1/replies/{postId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("존재하지 않는 게시글 입니다")));
    }

    @Test
    @DisplayName("POST /api/v1/replies/{postId} - 자식 댓글 생성 성공")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void createChildReply_Success() throws Exception {
        // 요청 DTO 생성
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("새 댓글", parentReply.getId());

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/replies/{postId}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
    @DisplayName("POST /api/v1/replies/{postId} - 부모 댓글 생성 성공")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void createParentReply_Success() throws Exception {
        // 요청 DTO 생성
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("새 댓글", null);

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/replies/{postId}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg", is("댓글이 생성되었습니다")))
                .andExpect(jsonPath("$.content", is("새 댓글")))
                .andExpect(jsonPath("$.memberName", is("테스트유저")))
                .andExpect(jsonPath("$.likeCount", is(0)))
                .andExpect(jsonPath("$.parentId", nullValue()))
                .andExpect(jsonPath("$.postId", is(testPost.getId().intValue())))
                .andExpect(jsonPath("$.replyId", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/replies/{postId} - 유효성 검사 실패 (내용 없음)")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void createReply_ValidationFail_NoContent() throws Exception {
        // 요청 DTO 생성 (내용 없음)
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("", null);

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/replies/{postId}", testPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글 내용을 작성해주세요")));
    }

    @Test
    @DisplayName("POST /api/v1/replies/{postId} - 존재하지 않는 게시글")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void createReply_PostNotFound() throws Exception {
        // 요청 DTO 생성
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("댓글 내용", null);

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/v1/replies/{postId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("존재하지 않는 게시글입니다")));
    }

    @Test
    @DisplayName("PATCH /api/v1/replies/{replyId} - 성공")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
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
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("댓글이 수정되었습니다")))
                .andExpect(jsonPath("$.content", is("수정된 댓글")));
    }

    @Test
    @DisplayName("PATCH /api/v1/replies/{replyId} - 유효성 검사 실패 (내용 없음)")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
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
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("수정 할 댓글 내용을 작성해주세요")));
    }

    @Test
    @DisplayName("PATCH /api/v1/replies/{replyId} - 존재하지 않는 댓글")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void updateReply_ReplyNotFound() throws Exception {
        // 요청 DTO 생성
        ReplyDTO.updateRequest request = new ReplyDTO.updateRequest("수정된 댓글");

        // WHEN
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/replies/{replyId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글을 찾을 수 없습니다")));
    }

    @Test
    @DisplayName("PATCH /api/v1/replies/{replyId} - 권한 없음")
    @WithUserDetails(value = "otheruser@example.com", userDetailsServiceBeanName = "securityUserService")
    void updateReply_Unauthorized() throws Exception {
        // 기존 댓글 생성 (testMember 소유)
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
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("댓글 작성자만 수정,삭제 할 수 있습니다")));
    }

    @Test
    @DisplayName("DELETE /api/v1/replies/{replyId} - 자식 댓글 삭제 성공")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void deleteChildReply_Success() throws Exception {
        // 자식 댓글 생성
        Reply childReply = Reply.builder()
                .content("삭제할 댓글")
                .member(testMember)
                .post(testPost)
                .parent(parentReply)
                .build();
        replyRepository.save(childReply);

        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/replies/{replyId}", childReply.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("자식 댓글 삭제 완료")))
                .andExpect(jsonPath("$.content", is("댓글이 삭제되었습니다")));

        // 데이터베이스에서 삭제 확인
        boolean exists = replyRepository.existsById(childReply.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("DELETE /api/v1/replies/{replyId} - 부모 댓글 삭제 성공")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void deleteParentReply_Success() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/replies/{replyId}", parentReply.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("부모 댓글 삭제 완료")))
                .andExpect(jsonPath("$.content", is("작성자에 의해 댓글이 삭제되었습니다")));

        // 데이터베이스에서 삭제 확인
        boolean exists = replyRepository.existsById(parentReply.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("DELETE /api/v1/replies/{replyId} - 존재하지 않는 댓글")
    @WithUserDetails(value = "testuser@example.com", userDetailsServiceBeanName = "securityUserService")
    void deleteReply_ReplyNotFound() throws Exception {
        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/replies/{replyId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("댓글을 찾을 수 없습니다")));
    }

    @Test
    @DisplayName("DELETE /api/v1/replies/{replyId} - 권한 없음")
    @WithUserDetails(value = "otheruser@example.com", userDetailsServiceBeanName = "securityUserService")
    void deleteReply_Unauthorized() throws Exception {
        // 기존 댓글 생성
        Reply existingReply = Reply.builder()
                .content("삭제할 댓글")
                .member(testMember)
                .post(testPost)
                .build();
        replyRepository.save(existingReply);

        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/replies/{replyId}", existingReply.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("댓글 작성자만 수정,삭제 할 수 있습니다")));
    }
}
