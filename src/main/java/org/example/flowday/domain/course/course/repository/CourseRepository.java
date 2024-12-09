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
    SELECT c, COALESCE(GROUP_CONCAT(s.placeId), '')
    FROM Course c
    LEFT JOIN c.spots s
    WHERE (c.member.id = :memberId
           OR (c.member.id = :partnerId AND c.status = 'COUPLE' AND :partnerId IS NOT NULL))
    GROUP BY c.id
    ORDER BY c.createdAt DESC
    """)
    List<Object[]> findAllByMemberIdOrPartnerId(Long memberId, Long partnerId);

}
