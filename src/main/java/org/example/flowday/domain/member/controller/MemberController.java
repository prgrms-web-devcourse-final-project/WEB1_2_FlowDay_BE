package org.example.flowday.domain.member.controller;


import org.example.flowday.domain.member.dto.MemberDTO;
import org.example.flowday.domain.member.exception.MemberTaskException;
import org.example.flowday.domain.member.service.MemberService;
import org.example.flowday.global.security.util.SecurityUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 보안 관련 엔드 포인트


    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestBody MemberDTO.TokenRefreshRequestDTO dto) {
        try {
            // JwtService를 통해 Access Token 갱신
            String newAccessToken = memberService.refreshAccessToken(dto.getToken());
            if(!Objects.equals(newAccessToken, "Invalid token")) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + newAccessToken);
                // 새로 발급된 토큰 반환
                return ResponseEntity.ok().headers(headers).body("newAccessToken");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (IllegalArgumentException e) {
            // 예외 발생 시 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (MemberTaskException e) {
            System.out.println(e.getMessage());
            // 기타 예외 발생 시 500 Error 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<Object> createMember(@RequestBody MemberDTO.CreateRequestDTO dto) {
        try {
            return ResponseEntity.ok(memberService.createMember(dto.toEntity()));
        } catch (MemberTaskException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    // 이메일로 아이디 찾기
    @PostMapping("/findId")
    public ResponseEntity<Object> findId(@RequestBody MemberDTO.FindIdRequestDTO dto){
        try{
        return ResponseEntity.ok(memberService.getMemberByEmail(dto.getEmail()));
    } catch (MemberTaskException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    // 비밀번호 찾기
    @PostMapping("/findPW")
    public ResponseEntity<String> findPw(@RequestBody MemberDTO.FindPWRequestDTO request) {
        try {
            // EmailService를 통해 임시 비밀번호 이메일 발송
            memberService.sendTempPasswordEmail(request.getLoginId(), request.getEmail());

            return ResponseEntity.ok("임시 비밀번호를 이메일로 전송했습니다");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("임시 비밀번호 전송을 실패하였습니다");
        }
    }

    // 로그인 ( Swagger 전용 )
    @PostMapping("/login")
    public ResponseEntity<String> login(@AuthenticationPrincipal SecurityUser user){
        return ResponseEntity.ok("{\"message\":\"Login successful\"}");
    }

    // 로그아웃 ( Swagger 전용 )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal SecurityUser user){
        return ResponseEntity.ok("{\"message\":\"Logout successful\"}");
    }







    // 내 정보 관련 엔드 포인트


    // 회원 조회 (마이 페이지)
    @GetMapping("/")
    public ResponseEntity<MemberDTO.MyPageResponseDTO> getMyPage(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(memberService.getMyPage(user.getId()));
    }

    // 회원 수정
    @PutMapping("/")
    public ResponseEntity<MemberDTO.UpdateResponseDTO> updateMember(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody MemberDTO.UpdateRequestDTO dto
    ) {
        try {
            return ResponseEntity.ok(memberService.updateMember(user.getId(), dto.toEntity()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 회원 삭제
    @DeleteMapping("/")
    public ResponseEntity<String> deleteMember(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(memberService.deleteMember(user.getId()));
    }

    // 프로필 이미지 수정
    @PutMapping("/updateImage")
    public ResponseEntity<MemberDTO.ChangeImageResponseDTO> modifyImage(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(memberService.changeProfileImage(user.getId(), image));
    }

    // 생일 수정(등록)
    @PutMapping("/birthday")
    public ResponseEntity<String> updateBirthday(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody MemberDTO.UpdateBirthdayRequestDTO dto
    ) {
        try {
            memberService.updateBirthday(user.getId(), dto.getBirthDt());
            return ResponseEntity.ok("birthday updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }







    // 연인 관련 엔드 포인트


    //연인 찾기
    @GetMapping("/partner/{name}")
    public ResponseEntity<MemberDTO.FindPartnerResponseDTO> getMemberByName(@PathVariable String name) {
        return ResponseEntity.ok(memberService.getPartner(name));
    }

    // 만나기 시작한 날 수정
    @PutMapping("/relationship")
    public ResponseEntity<String> updateRelationshipStartDate(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody MemberDTO.UpdateRelationshipStartDateRequestDTO dto
    ) {
        try {
            memberService.updateRelationshipStartDate(user.getId(), dto);
            return ResponseEntity.ok("start date updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 파트너 ID 수정
    // 테스트 전용 ( 알림 도메인 완성시 변경 예정 )
    @PutMapping("/partnerUpdate")
    public ResponseEntity<String> updatePartnerId(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody MemberDTO.UpdatePartnerIdRequestDTO dto
    ) {
        try {
            memberService.updatePartnerId(user.getId(), dto);
            return ResponseEntity.ok("partner updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 연결 끊기
    @PutMapping("/disconnect")
    public ResponseEntity<String> disconnect(@AuthenticationPrincipal SecurityUser user, @RequestParam Boolean stat){
        memberService.disconnectPartner(user.getId(), stat);
        return ResponseEntity.ok("disconnected");
    }







    // 기타


    // 회원 조회 (ID로 조회)
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO.ReadResponseDTO> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMember(id));
    }

}
