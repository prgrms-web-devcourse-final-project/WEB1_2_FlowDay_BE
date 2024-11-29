package org.example.flowday.domain.course.spot.repository;

import org.example.flowday.domain.course.spot.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    List<Spot> findAllByCourseIdOrderBySequenceAsc(Long courseId);
    List<Spot> findAllByVoteIdOrderBySequenceAsc(Long voteId);
    List<Spot> findAllByCity(String city);
    List<Spot> findAllByWishPlaceIdOrderByIdDesc(Long wishPlaceId);
}
