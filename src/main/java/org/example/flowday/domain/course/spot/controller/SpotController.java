package org.example.flowday.domain.course.spot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flowday.domain.course.spot.dto.SpotResDTO;
import org.example.flowday.domain.course.spot.service.SpotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/spots")
@Tag(name = "Spot", description = "장소 관련 api")
public class SpotController {

    private final SpotService spotService;

    // 지역별 인기 장소 top4
    @Operation(summary = "top4 조회", description = "지역별 인기 장소 top4 조회")
    @GetMapping("/top4")
    public ResponseEntity<List<SpotResDTO>> getTopSpotsByCity(@RequestParam String city) {
        List<SpotResDTO> topSpots = spotService.getTopSpotsByCity(city);
        return ResponseEntity.ok(topSpots);
    }

}
