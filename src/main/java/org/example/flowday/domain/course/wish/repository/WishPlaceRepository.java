package org.example.flowday.domain.course.wish.repository;

import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishPlaceRepository extends JpaRepository<WishPlace, Long> {

    Optional<WishPlace> findByMemberId(Long memberId);
    List<WishPlace> findAllByMemberId(Long memberId);

    @Query("""
    SELECT wp, COALESCE(GROUP_CONCAT(s.placeId), '') 
    FROM WishPlace wp
    LEFT JOIN wp.spots s
    WHERE wp.member.id IN (:memberIds)
    GROUP BY wp.id
    ORDER BY wp.id DESC
    """)
    List<Object[]> findAllWithSpotsByMemberIds(@Param("memberIds") List<Long> memberIds);

}
