package com.kbsw.seasonthon.route.controller;

import com.kbsw.seasonthon.route.dto.request.RouteRequest;
import com.kbsw.seasonthon.route.dto.response.RouteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Route", description = "라우트 관련 API")
public class RouteController {

    @PostMapping("/route-single")
    @Operation(summary = "단일 라우트 정보 조회", description = "라우트 ID로 라우트 정보를 조회합니다.")
    public ResponseEntity<RouteResponse> getRouteInfo(@RequestBody RouteRequest request) {
        log.info("라우트 정보 조회 요청: {}", request.getRouteId());
        
        // 임시 테스트 데이터 생성
        Random random = new Random();
        int safetyScore = 50 + random.nextInt(50); // 50-99 사이의 랜덤 점수
        
        List<List<Double>> waypoints = Arrays.asList(
            Arrays.asList(37.5665, 126.9780), // 서울시청
            Arrays.asList(37.5675, 126.9790), // 명동
            Arrays.asList(37.5670, 126.9760), // 을지로
            Arrays.asList(37.5665, 126.9780)  // 서울시청 (돌아오기)
        );
        
        RouteResponse response = RouteResponse.builder()
                .routeId(request.getRouteId())
                .type("safe")
                .distanceKm(5.1)
                .safetyScore(safetyScore)
                .durationMin(32)
                .waypoints(waypoints)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
