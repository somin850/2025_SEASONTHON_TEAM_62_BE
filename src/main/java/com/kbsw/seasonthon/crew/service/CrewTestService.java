package com.kbsw.seasonthon.crew.service;

import com.kbsw.seasonthon.crew.dto.request.CrewCreateRequest;
import com.kbsw.seasonthon.crew.dto.request.CrewSearchRequest;
import com.kbsw.seasonthon.crew.dto.request.CrewUpdateRequest;
import com.kbsw.seasonthon.crew.dto.request.ParticipantApprovalRequest;
import com.kbsw.seasonthon.crew.dto.response.CrewApplyResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewApprovalResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewCreateResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewDetailResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewListPageResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewListResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewUpdateResponse;
import com.kbsw.seasonthon.crew.enums.CrewStatus;
import com.kbsw.seasonthon.crew.enums.ParticipantStatus;
import com.kbsw.seasonthon.crew.enums.SafetyLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrewTestService {

    private final RestTemplate restTemplate;

    public CrewCreateResponse createCrew(CrewCreateRequest request) {
        // 라우트 정보 조회
        Map<String, Object> routeInfo = getRouteInfo(request.getRouteId());
        
        // waypoints를 String 리스트로 변환
        @SuppressWarnings("unchecked")
        List<List<Double>> waypointsList = (List<List<Double>>) routeInfo.get("waypoints");
        List<String> waypoints = waypointsList.stream()
                .map(point -> point.get(0) + "," + point.get(1))
                .toList();
        
        // 임시 크루 ID 생성
        Long crewId = System.currentTimeMillis() % 1000000L;
        
        return CrewCreateResponse.builder()
                .id(crewId)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(com.kbsw.seasonthon.crew.enums.CrewStatus.OPEN)
                .maxParticipants(request.getMaxParticipants())
                .routeId((String) routeInfo.get("routeId"))
                .type((String) routeInfo.get("type"))
                .distanceKm((Double) routeInfo.get("distanceKm"))
                .safetyScore((Integer) routeInfo.get("safetyScore"))
                .safetyLevel(SafetyLevel.fromScore((Integer) routeInfo.get("safetyScore")))
                .durationMin((Integer) routeInfo.get("durationMin"))
                .waypoints(waypoints)
                .tags(request.getTags() != null ? request.getTags() : List.of())
                .startLocation(request.getStartLocation())
                .pace(request.getPace())
                .startTime(request.getStartTime())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public CrewUpdateResponse updateCrew(Long crewId, CrewUpdateRequest request) {
        // 임시 업데이트 응답
        return CrewUpdateResponse.builder()
                .id(crewId)
                .status(com.kbsw.seasonthon.crew.enums.CrewStatus.OPEN)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void deleteCrew(Long crewId) {
        log.info("크루 삭제: {}", crewId);
        // 실제 삭제 로직은 생략
    }

    public CrewApplyResponse applyToCrew(Long crewId) {
        log.info("크루 참여 신청: {}", crewId);
        return CrewApplyResponse.builder()
                .status(ParticipantStatus.APPLIED)
                .build();
    }

    public CrewApprovalResponse approveParticipant(Long crewId, Long userId, ParticipantApprovalRequest request) {
        log.info("크루 참여 승인: crewId={}, userId={}, approve={}", crewId, userId, request.getApprove());
        
        ParticipantStatus status = request.getApprove() ? 
                ParticipantStatus.APPROVED : ParticipantStatus.REJECTED;
        
        return CrewApprovalResponse.builder()
                .status(status)
                .build();
    }

    public Map<String, Object> getRouteInfo(String routeId) {
        try {
            String url = "http://localhost:8080/api/route-single";
            Map<String, Object> requestBody = Map.of("routeId", routeId);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            
            return response;
        } catch (Exception e) {
            log.error("라우트 정보 조회 실패: {}", e.getMessage());
            // 임시 데이터 반환
            Random random = new Random();
            int safetyScore = 50 + random.nextInt(50);
            
            List<List<Double>> waypoints = Arrays.asList(
                Arrays.asList(37.5665, 126.9780),
                Arrays.asList(37.5675, 126.9790),
                Arrays.asList(37.5670, 126.9760),
                Arrays.asList(37.5665, 126.9780)
            );
            
            return Map.of(
                "routeId", routeId,
                "type", "safe",
                "distanceKm", 5.1,
                "safetyScore", safetyScore,
                "durationMin", 32,
                "waypoints", waypoints
            );
        }
    }

    // 크루 리스트 조회 (검색 및 필터링) - 테스트용
    public CrewListPageResponse searchCrews(CrewSearchRequest request) {
        // 테스트용 더미 데이터 생성
        List<CrewListResponse> dummyCrews = createDummyCrewList();
        
        // 간단한 필터링 로직 (현재 CrewSearchRequest에 있는 필드들만 사용)
        List<CrewListResponse> filteredCrews = dummyCrews.stream()
            // 검색 필드들 (조회용)
            .filter(crew -> request.getKeyword() == null || 
                    crew.getTitle().contains(request.getKeyword()) ||
                    crew.getDescription().contains(request.getKeyword()))
            .filter(crew -> request.getStartLocation() == null || 
                    (crew.getStartLocation() != null && crew.getStartLocation().contains(request.getStartLocation())))
            .filter(crew -> request.getStatus() == null || 
                    crew.getStatus().name().equalsIgnoreCase(request.getStatus()))
            .filter(crew -> request.getSafetyLevel() == null || 
                    crew.getSafetyLevel().name().equalsIgnoreCase(request.getSafetyLevel()))
            .filter(crew -> request.getTags() == null || request.getTags().isEmpty() ||
                    request.getTags().stream().anyMatch(tag -> crew.getTags().contains(tag)))
            // 실제 필터링 필드들
            .filter(crew -> request.getMaxDistance() == null || 
                    crew.getDistanceKm() <= request.getMaxDistance())
            .filter(crew -> request.getMinPace() == null || 
                    (crew.getPace() != null && comparePace(crew.getPace(), request.getMinPace()) >= 0))
            .filter(crew -> request.getStartTimeFrom() == null || 
                    (crew.getStartTime() != null && crew.getStartTime().isAfter(request.getStartTimeFrom())))
            .collect(Collectors.toList());
        
        // 정렬 적용
        applySorting(filteredCrews, request);
        
        // 페이징 처리
        int start = request.getPage() * request.getSize();
        int end = Math.min(start + request.getSize(), filteredCrews.size());
        List<CrewListResponse> pagedCrews = filteredCrews.subList(start, end);
        
        return CrewListPageResponse.builder()
            .crews(pagedCrews)
            .currentPage(request.getPage())
            .totalPages((int) Math.ceil((double) filteredCrews.size() / request.getSize()))
            .totalElements(filteredCrews.size())
            .size(request.getSize())
            .hasNext(end < filteredCrews.size())
            .hasPrevious(request.getPage() > 0)
            .isFirst(request.getPage() == 0)
            .isLast(end >= filteredCrews.size())
            .build();
    }
    

    
    // 크루 상세 조회 - 테스트용
    public CrewDetailResponse getCrewDetail(Long crewId) {
        // 테스트용 더미 데이터 생성
        return CrewDetailResponse.builder()
            .id(crewId)
            .title("테스트 크루 " + crewId)
            .description("테스트용 크루 상세 정보입니다.")
            .status(CrewStatus.OPEN)
            .hostName("테스트 호스트")
            .hostEmail("test@example.com")
            .maxParticipants(8)
            .currentParticipants(3)
            .routeId("test_route_" + crewId)
            .type("safe")
            .distanceKm(5.5)
            .safetyScore(85)
            .safetyLevel(SafetyLevel.SAFE)
            .durationMin(35)
            .waypoints(Arrays.asList(
                "37.5665,126.9780",
                "37.5675,126.9790",
                "37.5670,126.9760"
            ))
            .tags(Arrays.asList("자전거", "주말", "초보자"))
            .startLocation("경북대학교 정문")
            .pace("6'30\"/km")
            .startTime(LocalDateTime.now().plusHours(3))
            .participants(Arrays.asList(
                CrewDetailResponse.ParticipantInfo.builder()
                    .userId(1L)
                    .userName("참여자1")
                    .userEmail("user1@example.com")
                    .status("APPROVED")
                    .appliedAt(LocalDateTime.now().minusDays(1))
                    .build(),
                CrewDetailResponse.ParticipantInfo.builder()
                    .userId(2L)
                    .userName("참여자2")
                    .userEmail("user2@example.com")
                    .status("APPROVED")
                    .appliedAt(LocalDateTime.now().minusHours(5))
                    .build()
            ))
            .createdAt(LocalDateTime.now().minusDays(2))
            .updatedAt(LocalDateTime.now().minusHours(1))
            .build();
    }
    
    // 테스트용 더미 크루 리스트 생성
    private List<CrewListResponse> createDummyCrewList() {
        return Arrays.asList(
            CrewListResponse.builder()
                .id(1L)
                .title("한강 자전거 크루")
                .description("한강에서 자전거 타는 크루입니다.")
                .status(CrewStatus.OPEN)
                .hostName("호스트1")
                .maxParticipants(10)
                .currentParticipants(7)
                .routeId("route_1")
                .type("safe")
                .distanceKm(8.5)
                .safetyScore(90)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(45)
                            .waypoints(Arrays.asList("37.5665,126.9780", "37.5675,126.9790"))
            .tags(Arrays.asList("자전거", "한강", "주말"))
            .startLocation("한강공원 반포지구")
            .pace("5'30\"/km")
            .startTime(LocalDateTime.now().plusHours(2))
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusHours(2))
            .build(),
            CrewListResponse.builder()
                .id(2L)
                .title("도심 산책 크루")
                .description("도심에서 산책하는 크루입니다.")
                .status(CrewStatus.OPEN)
                .hostName("호스트2")
                .maxParticipants(5)
                .currentParticipants(3)
                .routeId("route_2")
                .type("medium")
                .distanceKm(3.2)
                .safetyScore(75)
                .safetyLevel(SafetyLevel.MEDIUM)
                .durationMin(25)
                            .waypoints(Arrays.asList("37.5665,126.9780", "37.5675,126.9790"))
            .tags(Arrays.asList("산책", "도심", "저녁"))
            .startLocation("명동역 2번 출구")
            .pace("8'00\"/km")
            .startTime(LocalDateTime.now().plusHours(1))
            .createdAt(LocalDateTime.now().minusHours(5))
            .updatedAt(LocalDateTime.now().minusHours(1))
            .build(),
            CrewListResponse.builder()
                .id(3L)
                .title("초보자 러닝 크루")
                .description("초보자를 위한 러닝 크루입니다.")
                .status(CrewStatus.OPEN)
                .hostName("호스트3")
                .maxParticipants(8)
                .currentParticipants(2)
                .routeId("route_3")
                .type("safe")
                .distanceKm(5.0)
                .safetyScore(85)
                .safetyLevel(SafetyLevel.SAFE)
                .durationMin(30)
                            .waypoints(Arrays.asList("37.5665,126.9780", "37.5675,126.9790"))
            .tags(Arrays.asList("러닝", "초보자", "아침"))
            .startLocation("경북대학교 정문")
            .pace("6'30\"/km")
            .startTime(LocalDateTime.now().plusHours(3))
            .createdAt(LocalDateTime.now().minusHours(3))
            .updatedAt(LocalDateTime.now().minusMinutes(30))
            .build()
        );
    }
    
    // 페이스 비교 헬퍼 메서드 (분/초 단위로 변환하여 비교)
    private int comparePace(String pace1, String pace2) {
        if (pace1 == null || pace2 == null) return 0;
        
        try {
            // "6'30\"/km" 형식을 분.초로 변환
            double minutes1 = parsePaceToMinutes(pace1);
            double minutes2 = parsePaceToMinutes(pace2);
            
            return Double.compare(minutes1, minutes2);
        } catch (Exception e) {
            return 0;
        }
    }
    
    private double parsePaceToMinutes(String pace) {
        // "6'30\"/km" -> 6.5분
        String cleanPace = pace.replaceAll("[^0-9':\"]", "");
        String[] parts = cleanPace.split("'");
        
        if (parts.length == 2) {
            int minutes = Integer.parseInt(parts[0]);
            String secondsStr = parts[1].replaceAll("[^0-9]", "");
            int seconds = secondsStr.isEmpty() ? 0 : Integer.parseInt(secondsStr);
            
            return minutes + (seconds / 60.0);
        }
        
        return 0.0;
    }
    
    // 정렬 적용
    private void applySorting(List<CrewListResponse> crews, CrewSearchRequest request) {
        String sortType = request.getSortType();
        String sortDirection = request.getSortDirection();
        
        if ("popular".equalsIgnoreCase(sortType)) {
            // 인기순: 참여자 수 기준 (내림차순)
            crews.sort((a, b) -> Integer.compare(b.getCurrentParticipants(), a.getCurrentParticipants()));
        } else if ("latest".equalsIgnoreCase(sortType)) {
            // 최신순: 생성일 기준 (내림차순)
            crews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        } else if ("distance".equalsIgnoreCase(sortType)) {
            // 거리순: 거리 기준
            if ("ASC".equalsIgnoreCase(sortDirection)) {
                crews.sort((a, b) -> Double.compare(a.getDistanceKm(), b.getDistanceKm()));
            } else {
                crews.sort((a, b) -> Double.compare(b.getDistanceKm(), a.getDistanceKm()));
            }
        } else if ("pace".equalsIgnoreCase(sortType)) {
            // 페이스순: 페이스 기준
            if ("ASC".equalsIgnoreCase(sortDirection)) {
                crews.sort((a, b) -> comparePace(a.getPace(), b.getPace()));
            } else {
                crews.sort((a, b) -> comparePace(b.getPace(), a.getPace()));
            }
        } else if ("time".equalsIgnoreCase(sortType)) {
            // 시작시간순: 시작시간 기준
            if ("ASC".equalsIgnoreCase(sortDirection)) {
                crews.sort((a, b) -> {
                    if (a.getStartTime() == null && b.getStartTime() == null) return 0;
                    if (a.getStartTime() == null) return 1;
                    if (b.getStartTime() == null) return -1;
                    return a.getStartTime().compareTo(b.getStartTime());
                });
            } else {
                crews.sort((a, b) -> {
                    if (a.getStartTime() == null && b.getStartTime() == null) return 0;
                    if (a.getStartTime() == null) return 1;
                    if (b.getStartTime() == null) return -1;
                    return b.getStartTime().compareTo(a.getStartTime());
                });
            }
        } else {
            // 기본: 생성일 기준
            if ("ASC".equalsIgnoreCase(sortDirection)) {
                crews.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
            } else {
                crews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            }
        }
    }
}
