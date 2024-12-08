package org.example.flowday.domain.course.course.repository;

import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByMemberId(Long memberId);
    List<Course> findAllByMemberIdAndStatus(Long memberId, Status status);

    @Query("""
    SELECT c FROM Course c
    LEFT JOIN FETCH c.spots s
    WHERE c.member.id = :memberId OR (c.member.id = :partnerId AND c.status = :status)
    ORDER BY c.createdAt DESC
    """)
    List<Course> findAllByMemberIdOrPartnerId(Long memberId, Long partnerId, Status status);

}
