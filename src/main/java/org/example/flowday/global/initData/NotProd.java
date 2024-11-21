package org.example.flowday.global.initData;


import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.comment.comment.service.ReplyService;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Profile("!prod")
public class NotProd {

    @Autowired
    @Lazy
    private NotProd self;

    private final ReplyService replyService;
    private final ReplyRepository  replyRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    @Bean
    ApplicationRunner initNotProd() {
        return args -> {
            work1();
        };
    }

    @Transactional
    public void work1() {

        if (postRepository.findAll().size() > 0) {
            return;
        }

        //멤버 생성
        // given - 테스트에 필요한 데이터 준비
        Member member = Member.builder()
                .name("member1")
                .email("test@test.com")
                .pw("1234")
                .phoneNum("010-1234-1234")
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        Member member2 = Member.builder()
                .name("member2")
                .email("test@test.com")
                .pw("1234")
                .phoneNum("010-1234-1234")
                .role(Role.USER)
                .build();
        memberRepository.save(member2);



        //게시글 생성
        Post post = Post.builder()
                .title("post1")
                .content("Test Post Content")
                .build();
        postRepository.save(post);

        Post post2 = Post.builder()
                .title("post2")
                .content("Test Post Content")
                .build();
        postRepository.save(post2);


    }
}

