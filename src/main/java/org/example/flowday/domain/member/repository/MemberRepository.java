package org.example.flowday.domain.member.repository;

import org.example.flowday.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByLoginId(String username);
    Optional<Member> findByLoginId(String loginId); // 로그인 ID로 검색

    @Query("SELECT m.refreshToken FROM Member m WHERE m.loginId = :loginId")
    Optional<String> findRefreshTokenByLoginId(String loginId);

    @Query("SELECT m.id FROM Member m WHERE m.loginId = :loginId")
    Optional<Long> findIdByLoginId(String loginId);

    @Query("SELECT m.loginId FROM Member m WHERE m.email = :email")
    Optional<String> findLoginIdByEmail(String email);
    Optional<Member> findByLoginIdAndEmail(String loginId , String email);
}

