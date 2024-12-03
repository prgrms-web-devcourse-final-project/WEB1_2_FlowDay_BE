package org.example.flowday.domain.course.spot.service;

import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.course.entity.Course;
import org.example.flowday.domain.course.course.exception.CourseException;
import org.example.flowday.domain.course.course.repository.CourseRepository;
import org.example.flowday.domain.course.spot.dto.SpotReqDTO;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.entity.Spot;
import org.example.flowday.domain.course.spot.exception.SpotException;
import org.example.flowday.domain.course.spot.repository.SpotRepository;
import org.example.flowday.domain.course.wish.entity.WishPlace;
import org.example.flowday.domain.course.wish.exception.WishPlaceException;
import org.example.flowday.domain.course.wish.repository.WishPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotService {

    private final SpotRepository spotRepository;
    private final CourseRepository courseRepository;
    private final WishPlaceRepository wishPlaceRepository;

    // 지역별 인기 장소 top4
    public List<SpotResDTO> getTopSpotsByCity(String city) {
        List<Spot> spots = spotRepository.findAllByCity(city);

        if (spots.isEmpty()) {
            return Collections.emptyList();
        }

        // placeId로 그룹화 후 가장 먼저 저장된 장소 선택
        Map<String, Spot> topSpots = spots.stream()
                .collect(Collectors.groupingBy(
                        Spot::getPlaceId,
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(Spot::getId)),
                                Optional::get)
                ));

        // 상위 4개 반환
        List<Spot> topSpotsList = topSpots.values().stream()
                .limit(4)
                .collect(Collectors.toList());

        return topSpotsList.stream()
                .map(spot -> new SpotResDTO(spot))
                .collect(Collectors.toList());
    }

    // comment 수정
    public SpotResDTO updateComment(Long spotId, SpotReqDTO spotReqDTO) {
        Spot spot = spotRepository.findById(spotId).orElseThrow(SpotException.NOT_FOUND::get);
        try {
            spot.changeComment(spotReqDTO.getComment());

            return new SpotResDTO(spot);
        } catch (Exception e) {
            e.printStackTrace();
            throw SpotException.NOT_UPDATED.get();
        }
    }

    // 장소 추가 : 장소 1개를 추가하는 공통 메서드
    public void addSpot(Long userId, Long entityId, SpotReqDTO spotReqDTO, String entityType) {
        try {
            if (entityType.equals("course")) {
                Course course = courseRepository.findById(entityId).orElseThrow(CourseException.NOT_FOUND::get);

                List<Integer> existingSequences = spotRepository.findAllByCourseIdOrderBySequenceAsc(course.getId())
                        .stream()
                        .map(Spot::getSequence)
                        .toList();

                int sequence = existingSequences.isEmpty() ? 1 : existingSequences.stream().max(Integer::compareTo).orElse(0) + 1;
                Spot spot = Spot.createSpot(spotReqDTO, sequence, course, null);

                spotRepository.save(spot);

            } else if (entityType.equals("wishPlace")) {
                WishPlace wishPlace = wishPlaceRepository.findByMemberId(userId).orElseThrow(WishPlaceException.NOT_FOUND::get);
                System.out.println("ㅎㅎㅎㅎㅎㅎ");
                System.out.println("spotReqDTO: " + spotReqDTO.getName());
                Spot spot = Spot.createSpot(spotReqDTO, 0, null, wishPlace);
                System.out.println("ㅋㅋ");
                System.out.println(userId);
                System.out.println("WishPlace: " + wishPlace);
                wishPlace.getSpots().add(spot);
                spotRepository.save(spot);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw SpotException.NOT_CREATED.get();
        }
    }

    // 장소 삭제 : 장소 1개를 삭제하는 공통 메서드
    public void removeSpot(Long userId, Long entityId, Long spotId, String entityType) {
        try {
            if (entityType.equals("course")) {
                Course course = courseRepository.findById(entityId).orElseThrow(CourseException.NOT_FOUND::get);

                Spot spotToRemove = course.getSpots().stream()
                        .filter(spot -> spot.getId().equals(spotId))
                        .findFirst()
                        .orElseThrow(SpotException.NOT_FOUND::get);

                course.getSpots().remove(spotToRemove);
                spotRepository.delete(spotToRemove);

                List<Spot> updatedSpots = course.getSpots().stream()
                        .sorted(Comparator.comparingInt(Spot::getSequence))
                        .toList();

                for (int i = 0; i < updatedSpots.size(); i++) {
                    Spot spot = updatedSpots.get(i);
                    spot.changeSequence(i + 1);
                    spotRepository.save(spot);
                }

            } else if (entityType.equals("wishPlace")) {
                WishPlace wishPlace = wishPlaceRepository.findByMemberId(userId).orElseThrow(WishPlaceException.NOT_FOUND::get);

                Spot spotToRemove = wishPlace.getSpots().stream()
                        .filter(spot -> spot.getId().equals(spotId))
                        .findFirst()
                        .orElseThrow(SpotException.NOT_FOUND::get);

                wishPlace.getSpots().remove(spotToRemove);
                spotRepository.delete(spotToRemove);
            }
        } catch (Exception e) {
            throw SpotException.NOT_DELETED.get();
        }
    }

}
