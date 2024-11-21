package org.example.flowday.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String pw;
    private String email;
    @Column(unique = true)
    private String name;
    private String phoneNum;
    private Long partnerId;
    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;
    private Role role;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime dateOfRelationshipStart;
    private LocalDateTime dateOfBirth;
}
