package org.example.flowday.domain.member.repository;

import org.example.flowday.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByLoginId(String username);
    Optional<Member> findByLoginId(String loginId); // 로그인 ID로 검색
    Optional<Member> findByEmail(String email); // 이메일로 검색
}

