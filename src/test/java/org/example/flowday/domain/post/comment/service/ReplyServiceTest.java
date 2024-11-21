package org.example.flowday.domain.post.comment.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.dto.ReplyDTO;
import org.example.flowday.domain.post.comment.entity.Reply;
import org.example.flowday.domain.post.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReplyServiceTest {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager em;


    @Test
    @DisplayName("부모 댓글 생성")
    public void v1() {

        //given
        Member member = Member.builder()
                .name("test")
                .email("test@test.com")
                .pw("1234")
                .phoneNum("010-1234-1234")
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);

        //when
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("댓글1", null);
        ReplyDTO.createResponse response = replyService.saveReply(request, member.getId(), post.getId());

        //then
        Assertions.assertThat(response.getParentId()).isNull();
        Assertions.assertThat(response.getMemberName()).isEqualTo(member.getName());
        Assertions.assertThat(response.getPostId()).isEqualTo(post.getId());

    }


    @Test
    @DisplayName("자식 댓글 생성")
    public void v2() {

        //given
        Member member = Member.builder()
                .name("test")
                .email("test@test.com")
                .pw("1234")
                .phoneNum("010-1234-1234")
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);


        ReplyDTO.createRequest request = new ReplyDTO.createRequest("부모댓글1", null);
        ReplyDTO.createResponse response = replyService.saveReply(request, member.getId(), post.getId());


        //when
        ReplyDTO.createRequest request2 = new ReplyDTO.createRequest("자식댓글1", response.getReplyId());
        ReplyDTO.createResponse response2 = replyService.saveReply(request2, member.getId(), post.getId());


        Reply parentReply = replyRepository.findById(response.getReplyId()).get();


        //then
        Assertions.assertThat(response2.getParentId()).isEqualTo(parentReply.getId());
        Assertions.assertThat(response2.getMemberName()).isEqualTo(member.getName());
        Assertions.assertThat(response2.getPostId()).isEqualTo(post.getId());
        Assertions.assertThat(parentReply.getChildren().size()).isEqualTo(1);
        Assertions.assertThat(parentReply.getChildren().get(0).getParent().getId()).isEqualTo(parentReply.getId());


    }

    @Test
    @DisplayName("댓글 삭제 ")
    void t3() {
        //given
        Member member = Member.builder()
                .name("test")
                .email("test@test.com")
                .pw("1234")
                .phoneNum("010-1234-1234")
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        postRepository.save(post);


        ReplyDTO.createRequest request = new ReplyDTO.createRequest("부모댓글1", null);
        ReplyDTO.createResponse response = replyService.saveReply(request, member.getId(), post.getId());


        ReplyDTO.createRequest request2 = new ReplyDTO.createRequest("자식댓글1", response.getReplyId());
        ReplyDTO.createResponse response2 = replyService.saveReply(request2, member.getId(), post.getId());


        //when
        replyService.removeReply(response2.getReplyId(),member.getId()); // 자식 댓글 삭제
        replyService.removeReply(response.getReplyId(),member.getId()); // 부모 댓글 삭제
        Reply parentReply = replyRepository.findById(response.getReplyId()).get();

        em.flush();

        //then
        Assertions.assertThat(parentReply.getChildren().size()).isEqualTo(0);
        Assertions.assertThat(parentReply.getContent()).isEqualTo("작성자에 의해 삭제되었습니다");
        Assertions.assertThat(replyRepository.findAll().size()).isEqualTo(1);


    }

    @Test
    @DisplayName("댓글 조회")
    public void t4() {
        // given - 테스트에 필요한 데이터 준비
        Member member = Member.builder()
                .name("test")
                .email("test@test.com")
                .pw("1234")
                .phoneNum("010-1234-1234")
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("Test Post Title")
                .content("Test Post Content")
                .build();
        postRepository.save(post);

        // 부모 댓글 생성
        ReplyDTO.createRequest parentRequest1 = new ReplyDTO.createRequest("부모댓글1", null);
        ReplyDTO.createResponse parentResponse1 = replyService.saveReply(parentRequest1, member.getId(), post.getId());

        ReplyDTO.createRequest parentRequest2 = new ReplyDTO.createRequest("부모댓글2", null);
        ReplyDTO.createResponse parentResponse2 = replyService.saveReply(parentRequest2, member.getId(), post.getId());

        // 자식 댓글 생성 - 부모댓글1에 대한 자식 댓글
        ReplyDTO.createRequest childRequest1 = new ReplyDTO.createRequest("자식댓글1-1", parentResponse1.getReplyId());
        replyService.saveReply(childRequest1, member.getId(), post.getId());

        ReplyDTO.createRequest childRequest2 = new ReplyDTO.createRequest("자식댓글1-2", parentResponse1.getReplyId());
        replyService.saveReply(childRequest2, member.getId(), post.getId());

        // 자식 댓글 생성 - 부모댓글2에 대한 자식 댓글
        ReplyDTO.createRequest childRequest3 = new ReplyDTO.createRequest("자식댓글2-1", parentResponse2.getReplyId());
        replyService.saveReply(childRequest3, member.getId(), post.getId());

        ReplyDTO.createRequest childRequest4 = new ReplyDTO.createRequest("자식댓글2-2", parentResponse2.getReplyId());
        replyService.saveReply(childRequest4, member.getId(), post.getId());

        // when - 댓글 조회 실행
        List<ReplyDTO.Response> replies = replyService.findAllByPost(post.getId());

        // then - 결과 검증
        Assertions.assertThat(replies.size()).isEqualTo(2); // 부모 댓글은 두 개여야 함

        ReplyDTO.Response parentReply1 = replies.get(0);
        Assertions.assertThat(parentReply1.getContent()).isEqualTo("부모댓글1");
        Assertions.assertThat(parentReply1.getChildren().size()).isEqualTo(2); // 부모댓글1의 자식 댓글 두 개여야 함
        Assertions.assertThat(parentReply1.getChildren().get(0).getContent()).isEqualTo("자식댓글1-1");
        Assertions.assertThat(parentReply1.getChildren().get(1).getContent()).isEqualTo("자식댓글1-2");

        ReplyDTO.Response parentReply2 = replies.get(1);
        Assertions.assertThat(parentReply2.getContent()).isEqualTo("부모댓글2");
        Assertions.assertThat(parentReply2.getChildren().size()).isEqualTo(2); // 부모댓글2의 자식 댓글 두 개여야 함
        Assertions.assertThat(parentReply2.getChildren().get(0).getContent()).isEqualTo("자식댓글2-1");
        Assertions.assertThat(parentReply2.getChildren().get(1).getContent()).isEqualTo("자식댓글2-2");
    }

}




