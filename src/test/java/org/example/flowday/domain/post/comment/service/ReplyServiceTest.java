package org.example.flowday.domain.post.comment.service;

import org.assertj.core.api.Assertions;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.member.service.MemberService;
import org.example.flowday.domain.post.comment.ReplyDTO;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        ReplyDTO.createRequest request = new ReplyDTO.createRequest("댓글1",post.getId(),null);
        ReplyDTO.createResponse response = replyService.saveReply(request, member.getId());

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


        ReplyDTO.createRequest request = new ReplyDTO.createRequest("부모댓글1",post.getId(),null);
        replyService.saveReply(request, member.getId());


        //when
        ReplyDTO.createRequest request2 = new ReplyDTO.createRequest("자식댓글1",post.getId(),1L);
        ReplyDTO.createResponse response2 = replyService.saveReply(request2, member.getId());


        Reply parentReply = replyRepository.findById(1L).get();


        //then
        Assertions.assertThat(response2.getParentId()).isEqualTo(1L);
        Assertions.assertThat(response2.getMemberName()).isEqualTo(member.getName());
        Assertions.assertThat(response2.getPostId()).isEqualTo(post.getId());
        Assertions.assertThat(parentReply.getChildren().size()).isEqualTo(1);
        Assertions.assertThat(parentReply.getChildren().get(0).getParent().getId()).isEqualTo(1L);



    }


}