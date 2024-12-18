package org.example.flowday.domain.member.repository;

import jakarta.transaction.Transactional;
import org.example.flowday.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByLoginId(String username);

    boolean existsById(Long id);

    Optional<Member> findById(Long id);

    @Query("SELECT m.id AS id, m.loginId AS loginId, m.role AS role, m.pw AS pw FROM Member m WHERE m.loginId = :loginId")
    Optional<Map<String,Object>> findSecurityInfoByLoginId(@Param("loginId")String loginId);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.refreshToken = :refreshToken WHERE m.loginId = :loginId")
    void updateRefreshToken(@Param("loginId") String loginId, @Param("refreshToken") String refreshToken);

    @Query("SELECT " +
            "m1.name AS name, " +
            "m2.name AS partnerName, " +
            "m1.relationshipDt AS relationshipDt, " +
            "m1.birthDt AS birthDt, " +
            "m1.partnerId AS partnerId, " +
            "m1.chattingRoomId AS roomId " +
            "FROM Member m1 " +
            "LEFT JOIN Member m2 ON m1.partnerId = m2.id " +
            "WHERE m1.id = :id")
    Optional<Map<String, Object>> findMyPageById(@Param("id") Long id);

    @Query("SELECT m.refreshToken FROM Member m WHERE m.loginId = :loginId")
    Optional<String> findRefreshTokenByLoginId(String loginId);

    @Query("SELECT m.id FROM Member m WHERE m.loginId = :loginId")
    Optional<Long> findIdByLoginId(String loginId);

    @Query("SELECT m.id AS id, m.name AS name, m.email AS email FROM Member m WHERE m.name = :name")
    Optional<Map<String, Object>> findByName(String name);

    @Query("SELECT m.loginId FROM Member m WHERE m.email=:email AND m.name=:name")
    Optional<String> findByEmailAndName(String email, String name);

    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.pw = :pw WHERE m.loginId = :loginId AND m.email = :email")
    void UpdatePasswordByLoginIdAndEmail(@Param("loginId") String loginId , @Param("email")String email, @Param("pw") String pw);

}

