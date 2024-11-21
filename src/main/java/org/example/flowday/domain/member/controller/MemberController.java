package org.example.flowday.domain.member.controller;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.dto.MemberDTO;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.service.MemberService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    //메일 발송을 위한 JavaMailSender
    private final JavaMailSender mailSender;

    public MemberController(MemberService memberService, JavaMailSender mailSender) {
        this.memberService = memberService;
        this.mailSender = mailSender;
    }


    // 회원 가입
    @PostMapping
    //Entity -> DTO
    public ResponseEntity<MemberDTO.CreateResponseDTO> createMember(@RequestBody MemberDTO.CreateRequestDTO dto) {
        return ResponseEntity.ok(memberService.createMember(dto.toEntity()));
    }

    // 회원 조회 (ID로 조회)
    @GetMapping("/{id}")
    //Entity -> DTO
    public ResponseEntity<MemberDTO.ReadResponseDTO> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMember(id));
    }


    // 회원 수정
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody MemberDTO.UpdateRequestDTO dto) {
        try {
            Member updated = memberService.updateMember(id, dto.toEntity());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    // 프로필 이미지 수정
    @PutMapping("/updateImage")
    public ResponseEntity<MemberDTO.ChangeImageResponseDTO> modifyImage(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(memberService.changeProfileImage(user.getId(), image));
    }

    // 이메일로 아이디 찾기
    @GetMapping("/findId")
    public ResponseEntity<MemberDTO.FindIdResponseDTO> findId(@RequestBody String email){
        return ResponseEntity.ok(memberService.getMemberByEmail(email));
    }

    // 로그인id, 이메일로 비밀번호 변경 메일 발송
    @PostMapping("/findPW")
    public ResponseEntity<String> findPw(@RequestBody MemberDTO.FindPWRequestDTO request) {
        String templatePassword = memberService.setTemplatePassword(request.getLoginId(), request.getEmail());

        String title = "FlowDay 비밀번호 재설정 이메일 입니다.";
        String from= "jy037211@gmail.com";
        String to = request.getEmail();
        String content =
                System.lineSeparator() +
                        System.lineSeparator() +
                        "임시 비밀번호로 로그인 후 꼭 새로운 비밀번호로 설정해주시기 바랍니다."
                        + System.lineSeparator() +
                        System.lineSeparator() +
                        "임시비밀번호는 " +templatePassword+ " 입니다. "
                        + System.lineSeparator();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setFrom(from);
            messageHelper.setTo(to);
            messageHelper.setSubject(title);
            messageHelper.setText(content);

            mailSender.send(message);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("임시 비밀번호 전송을 실패하였습니다" );
        }
        return ResponseEntity.ok("임시 비밀번호를 이메일로 전송했습니다");
    }
}
