package org.example.flowday.global.initData;


import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseReqDTO;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.course.service.CourseService;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
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
                .loginId("login1")
                .pw("1234")
                .phoneNum("010-1234-1234")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member);

        Member member2 = Member.builder()
                .name("member2")
                .email("test2@test.com")
                .loginId("login2")
                .pw("1234")
                .phoneNum("010-1234-1234")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member2);

        // 장소 생성
        SpotReqDTO spot1 = SpotReqDTO.builder()
                .city("서울 종로")
                .placeId("place1")
                .name("카페")
                .comment("코멘트")
                .sequence(1)
                .build();
        SpotReqDTO spot2 = SpotReqDTO.builder()
                .city("서울 종로")
                .placeId("place2")
                .name("밥집")
                .comment("코멘트")
                .sequence(1)
                .build();
        SpotReqDTO spot3 = SpotReqDTO.builder()
                .city("서울 종로")
                .placeId("place3")
                .name("영화관")
                .comment("코멘트")
                .sequence(1)
                .build();

        List<SpotReqDTO> spots = new ArrayList<>();
        spots.add(spot1);
        spots.add(spot2);

        CourseReqDTO courseRequest = CourseReqDTO.builder()
                .color("color")
                .title("코스1")
                .date(LocalDate.now())
                .status(Status.COUPLE)
                .memberId(member.getId())
                .spots(spots)
                .build();

        CourseResDTO courseResDTO = courseService.saveCourse(courseRequest);
        Course course = courseRepository.findById(courseResDTO.getId()).get();

        Post post = Post.builder()
                .writer(member)
                .contents("게시글 내용")
                .title("게시글 제목 ")
                .status(org.example.flowday.domain.post.post.entity.Status.PUBLIC)
                .course(course)
                .build();
        Post savePost = postRepository.save(post);

        postService.addGenFileByUrl(savePost, "common", "inbody", 1, "https://picsum.photos/200/300");
        postService.addGenFileByUrl(savePost, "common", "inbody", 2, "https://picsum.photos/200/300");


    }
}

