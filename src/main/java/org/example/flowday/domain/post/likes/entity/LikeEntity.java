package org.example.flowday.domain.post.likes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.post.post.entity.Post;

import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "likes")
//@Getter
//@Setter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
//public class LikeEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Member member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Post post;
//
//
//
//}