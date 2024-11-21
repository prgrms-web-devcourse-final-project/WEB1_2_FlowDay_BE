package org.example.flowday.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.dto.MemberDTO;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.exception.MemberException;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public MemberDTO.CreateResponseDTO createMember(Member member) {
        member.setPw(passwordEncoder.encode(member.getPw()));
        member.setRole(Role.ROLE_USER);
        Member savedMember = memberRepository.save(member);
        return new MemberDTO.CreateResponseDTO(
                savedMember.getId(),
                savedMember.getLoginId(),
                savedMember.getEmail(),
                savedMember.getName(),
                savedMember.getPhoneNum()
        );
    }

    // 회원 조회
    public MemberDTO.ReadResponseDTO getMember(Long id) {
        try {
            Member member = memberRepository.findById(id).get();
            return new MemberDTO.ReadResponseDTO(
                    member.getName(),
                    member.getProfileImage(),
                    member.getPartnerId(),
                    member.getDateOfRelationshipStart(),
                    member.getDateOfBirth()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error getting member", e);
        }
    }

    // 로그인 ID로 회원 조회
    public MemberDTO.FindIdResponseDTO getMemberByEmail(String email) {
        return new MemberDTO.FindIdResponseDTO(memberRepository.findLoginIdByEmail(email).get());
    }

    // 회원 수정
    @Transactional
    public Member updateMember(Long id, Member updatedMember) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        if(!updatedMember.getLoginId().isEmpty()) {member.setLoginId(updatedMember.getLoginId());}
        if(!updatedMember.getPw().isEmpty()) {member.setPw(passwordEncoder.encode(updatedMember.getPw()));}
        if(!updatedMember.getEmail().isEmpty()) {member.setEmail(updatedMember.getEmail());}
        if(!updatedMember.getPhoneNum().isEmpty()) {member.setPhoneNum(updatedMember.getPhoneNum());}
        if(!updatedMember.getName().isEmpty()) {member.setName(updatedMember.getName());}
        return memberRepository.save(member);
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    //이미지 변경
    @Transactional
    public MemberDTO.ChangeImageResponseDTO changeProfileImage(Long id, MultipartFile imageFile){

        Optional<Member> memberOp = memberRepository.findById(id);
        if (memberOp.isPresent()) {
            Member member = memberOp.get();
            String fileName = saveImage(imageFile);
            member.setProfileImage(fileName);
            memberRepository.save(member);
            return new MemberDTO.ChangeImageResponseDTO(member.getId(), imageFile.getOriginalFilename());
        } else {
            throw MemberException.MEMBER_IMAGE_NOT_MODIFIED.getMemberTaskException();
        }
    }

    // 사용자가 보낸 이미지 파일 저장
    private String saveImage(MultipartFile imageFile) { // MultipartFile로 변경
        String uploadDir = "upload/";
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // 디렉토리가 존재하지 않으면 생성
            }

            // 파일 이름 생성 및 저장
            String ident = String.valueOf(System.currentTimeMillis()).substring(3, 10);
            String fileName = ident + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath); // 실제 파일 저장

            return fileName; // 저장된 파일 이름 반환
        } catch (IOException e) {
            throw MemberException.MEMBER_IMAGE_NOT_SAVED.getMemberTaskException();
        }
    }

    // 비밀번호 찾기 시, 임시로 적용할 비밀번호 생성
    @Transactional
    public String setTemplatePassword(String loginId, String email) {

        String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

        Member member = memberRepository.findByLoginIdAndEmail(loginId, email).orElseThrow(MemberException.MEMBER_NOT_FOUND::getMemberTaskException);

        SecureRandom random = new SecureRandom();
        StringBuilder tempPassword = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHAR_SET.length());
            tempPassword.append(CHAR_SET.charAt(index));
        }

        String templatePassword = tempPassword.toString();

        member.setPw(passwordEncoder.encode(templatePassword));
        memberRepository.save(member);

        return templatePassword;
    }
}