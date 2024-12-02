package org.example.flowday.domain.course.vote.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.dto.CourseResDTO;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.exception.CourseException;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.exception.SpotException;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.vote.dto.VoteReqDTO;
import org.example.flowday.domain.course.vote.dto.VoteResDTO;
import org.example.flowday.domain.course.vote.entity.Vote;
import org.example.flowday.domain.course.vote.exception.VoteException;
import org.example.flowday.domain.course.vote.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final CourseRepository courseRepository;
    private final SpotRepository spotRepository;
    private final VoteRepository voteRepository;

    // 투표 생성
    public VoteResDTO saveVote(Long userId, VoteReqDTO voteReqDTO) {
        Course course = courseRepository.findById(voteReqDTO.getCourseId()).orElseThrow(CourseException.NOT_FOUND::get);

        if (!userId.equals(course.getMember().getId()) && !userId.equals(course.getMember().getPartnerId())) {
            throw CourseException.FORBIDDEN.get();
        }

        try {
            Vote vote = Vote.createVote(course, voteReqDTO.getTitle());
            voteRepository.save(vote);

            List<Spot> spots = spotRepository.findAllById(voteReqDTO.getSpotIds());
            spots.forEach(spot -> spot.changeVote(vote));

            return new VoteResDTO(vote, convertToSpotResDTOs(spots));
        } catch (Exception e) {
            throw VoteException.NOT_CREATED.get();
        }
    }

    // 투표 조회
    public VoteResDTO findVote(Long voteId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(VoteException.NOT_FOUND::get);
        List<Spot> spots = spotRepository.findAllByVoteIdOrderBySequenceAsc(vote.getId());

        return new VoteResDTO(vote, convertToSpotResDTOs(spots));
    }

    // 투표 완료 후 코스 수정
    @Transactional
    public CourseResDTO updateCourseByVote(Long voteId, Long selectedSpotId) {
        try {
            Vote vote = voteRepository.findById(voteId).orElseThrow(VoteException.NOT_FOUND::get);
            Spot selectedSpot = spotRepository.findById(selectedSpotId).orElseThrow(SpotException.NOT_FOUND::get);
            Course course = vote.getCourse();

            // 선택한 장소 투표 연관 관계 제거
            if (selectedSpot.getVote() != null && selectedSpot.getVote().getId().equals(voteId)) {
                selectedSpot.removeVote();
                spotRepository.save(selectedSpot);
            }

            // 선택하지 않은 장소들 삭제
            List<Spot> spotsToDelete = spotRepository.findAllByVoteIdOrderBySequenceAsc(vote.getId());
            for (Spot spotToDelete : spotsToDelete) {
                spotToDelete.removeVote();
                spotRepository.save(spotToDelete);
                spotRepository.delete(spotToDelete);
                spotRepository.flush();
            }

            voteRepository.delete(vote);

            // 장소 순서 재배치
            List<Spot> updatedSpots = spotRepository.findAllByCourseIdOrderBySequenceAsc(course.getId());
            updatedSpots.sort(Comparator.comparingInt(Spot::getSequence));

            for (int i = 0; i < updatedSpots.size(); i++) {
                updatedSpots.get(i).changeSequence(i + 1);
                spotRepository.save(updatedSpots.get(i));
            }

            return new CourseResDTO(vote.getCourse(), convertToSpotResDTOs(updatedSpots));
        } catch (Exception e) {
            throw CourseException.NOT_UPDATED.get();
        }
    }

    private List<SpotResDTO> convertToSpotResDTOs(List<Spot> spots) {
        return spots.stream()
                .map(SpotResDTO::new)
                .collect(Collectors.toList());
    }

}
