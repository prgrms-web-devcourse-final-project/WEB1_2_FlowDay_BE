package org.example.flowday.global.initData;


import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.course.service.CourseService;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.domain.post.comment.comment.entity.Reply;
import org.example.flowday.domain.post.comment.comment.repository.ReplyRepository;
import org.example.flowday.domain.post.comment.comment.service.ReplyService;
import org.example.flowday.domain.post.post.entity.Post;
import org.example.flowday.domain.post.post.repository.PostRepository;
import org.example.flowday.domain.post.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final CourseRepository  courseRepository;
    private final SpotRepository spotRepository;
    private final PostService postService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private CourseService courseService;


    @Bean
    ApplicationRunner initNotProd() {
        return args -> {
            work1();
        };
    }

    @Transactional
    public void work1() {

        if (memberRepository.findAll().size() > 0) {
            return;
        }

        //멤버 생성
        // given - 테스트에 필요한 데이터 준비
        Member member = Member.builder()
                .name("member1")
                .email("test1@test.com")
                .loginId("test1")
                .pw(passwordEncoder.encode("1234"))
                .phoneNum("010-1234-1234")
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);

        Member member2 = Member.builder()
                .name("member2")
                .email("test2@test.com")
                .loginId("test2")
                .pw(passwordEncoder.encode("1234"))
                .phoneNum("010-1234-1234")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member2);

        member.setPartnerId(member2.getId());
        member2.setPartnerId(member.getId());

        memberRepository.save(member);
        memberRepository.save(member2);


        //코스 생성
        Course course = Course.builder()
                .title("코스1")
                .color("blue")
                .status(Status.COUPLE)
                .member(member)
                .build();

        courseRepository.save(course);

        // 장소 생성
        Spot spot1 = Spot.builder()
                .city("서울 종로")
                .placeId("place1")
                .name("카페")
                .comment("코멘트")
                .course(course)
                .build();

        Spot spot2 = Spot.builder()
                .city("서울 종로")
                .placeId("place2")
                .name("밥집")
                .comment("코멘트")
                .course(course)
                .build();

        Spot spot3 = Spot.builder()
                .city("서울 종로")
                .placeId("place3")
                .name("영화관")
                .comment("코멘트")
                .course(course)
                .build();

        spotRepository.save(spot1);
        spotRepository.save(spot2);
        spotRepository.save(spot3);


        Post post1 = Post.builder()
                .writer(member)
                .title("title 111")
                .contents("contents 111")
                .status(org.example.flowday.domain.post.post.entity.Status.COUPLE)
                .region("seoul1 seoul2")
                .season("spring summer")
                .build();

        postRepository.save(post1);

        Post post2 = Post.builder()
                .writer(member2)
                .title("title 222")
                .contents("contents 222")
                .status(org.example.flowday.domain.post.post.entity.Status.COUPLE)
                .region("seoul22 seoul22")
                .season("spring22 summer22")
                .build();

        postRepository.save(post2);

        Reply reply1 = Reply.builder()
                .content("부모댓글1")
                .post(post1)
                .parent(null)
                .member(member)
                .build();

        replyRepository.save(reply1);

        Reply reply2 = Reply.builder()
                .content("자식 댓글1")
                .post(post1)
                .parent(reply1)
                .member(member)
                .build();

        replyRepository.save(reply2);



    }
}

