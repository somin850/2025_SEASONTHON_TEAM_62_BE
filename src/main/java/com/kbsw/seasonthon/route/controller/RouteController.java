package com.kbsw.seasonthon.route.controller;

import com.kbsw.seasonthon.route.dto.request.RouteRequest;
import com.kbsw.seasonthon.route.dto.response.RouteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @PostMapping("/selected-route")
    @Operation(summary = "선택된 경로 저장", description = "사용자가 선택한 경로를 저장합니다.")
    public ResponseEntity<Map<String, Object>> saveSelectedRoute(@RequestBody Map<String, Object> routeData) {
        log.info("선택된 경로 저장 요청: {}", routeData.get("route_id"));
        
        try {
            // AI 모델에 선택된 경로 전송
            String aiServiceUrl = "http://43.202.57.158:5000";
            String url = aiServiceUrl + "/api/selected-route";
            
            // AI 모델에 전송할 데이터 준비
            Map<String, Object> requestBody = Map.of(
                "route_id", routeData.getOrDefault("route_id", "unknown"),
                "type", routeData.getOrDefault("type", "safe"),
                "distance_km", routeData.getOrDefault("distance_km", 5.0),
                "safety_score", routeData.getOrDefault("safety_score", 50),
                "estimated_time_min", routeData.getOrDefault("estimated_time_min", 30),
                "waypoints", routeData.getOrDefault("waypoints", List.of())
            );
            
            // RestTemplate을 사용하여 AI 모델에 전송
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            
            if (response != null && "success".equals(response.get("status"))) {
                log.info("경로 저장 성공: {}", response.get("route_id"));
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "경로가 성공적으로 저장되었습니다.",
                    "route_id", response.get("route_id")
                ));
            } else {
                log.warn("경로 저장 실패: {}", response);
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "경로 저장에 실패했습니다.",
                    "error", response != null ? response.get("error") : "Unknown error"
                ));
            }
            
        } catch (Exception e) {
            log.error("경로 저장 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "경로 저장 중 오류가 발생했습니다.",
                "error", e.getMessage()
            ));
        }
    }
}


