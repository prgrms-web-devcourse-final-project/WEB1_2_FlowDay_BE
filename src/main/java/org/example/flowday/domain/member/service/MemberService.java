package org.example.flowday.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    // 회원 가입
    @Transactional
    public Member createMember(Member member) {
        member.setPw(passwordEncoder.encode(member.getPw()));
        return memberRepository.save(member);
    }

    // 회원 조회
    public Optional<Member> getMember(Long id) {
        return memberRepository.findById(id);
    }

    // 로그인 ID로 회원 조회
    public Optional<Member> getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    // 이메일로 회원 조회
    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
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
}