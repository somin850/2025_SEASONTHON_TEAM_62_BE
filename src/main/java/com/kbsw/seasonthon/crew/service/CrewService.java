package com.kbsw.seasonthon.crew.service;

import com.kbsw.seasonthon.crew.domain.Crew;
import com.kbsw.seasonthon.crew.domain.CrewParticipant;
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
import com.kbsw.seasonthon.crew.repository.CrewParticipantRepository;
import com.kbsw.seasonthon.crew.repository.CrewRepository;
import com.kbsw.seasonthon.global.base.response.exception.BusinessException;
import com.kbsw.seasonthon.global.base.response.exception.ExceptionType;
import com.kbsw.seasonthon.user.entity.User;
import com.kbsw.seasonthon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewParticipantRepository crewParticipantRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public CrewCreateResponse createCrew(CrewCreateRequest request, User user) {
        // 라우트 정보 조회
        Map<String, Object> routeInfo = getRouteInfo(request.getRouteId());
        
        // waypoints를 String 리스트로 변환
        @SuppressWarnings("unchecked")
        List<List<Double>> waypointsList = (List<List<Double>>) routeInfo.get("waypoints");
        List<String> waypoints = waypointsList.stream()
                .map(point -> point.get(0) + "," + point.get(1))
                .toList();
        
        Crew crew = Crew.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .host(user)
                .maxParticipants(request.getMaxParticipants())
                .routeId((String) routeInfo.get("route_id"))
                .type((String) routeInfo.get("type"))
                .distanceKm((Double) routeInfo.get("distance_km"))
                .safetyScore(((Double) routeInfo.get("safety_score")).intValue())
                .safetyLevel(SafetyLevel.fromScore(((Double) routeInfo.get("safety_score")).intValue()))
                .durationMin(((Double) routeInfo.get("duration_min")).intValue())
                .waypoints(waypoints)
                .tags(request.getTags() != null ? request.getTags() : List.of())
                .startLocation(request.getStartLocation())
                .pace(request.getPace())
                .startTime(request.getStartTime())
                .build();
        
        crewRepository.save(crew);
        
        return CrewCreateResponse.builder()
                .id(crew.getId())
                .title(crew.getTitle())
                .description(crew.getDescription())
                .status(crew.getStatus())
                .maxParticipants(crew.getMaxParticipants())
                .routeId(crew.getRouteId())
                .type(crew.getType())
                .distanceKm(crew.getDistanceKm())
                .safetyScore(crew.getSafetyScore())
                .safetyLevel(crew.getSafetyLevel())
                .durationMin(crew.getDurationMin())
                .waypoints(crew.getWaypoints())
                .tags(crew.getTags())
                .startLocation(crew.getStartLocation())
                .pace(crew.getPace())
                .startTime(crew.getStartTime())
                .createdAt(crew.getCreatedAt())
                .build();
    }

    public CrewUpdateResponse updateCrew(Long crewId, CrewUpdateRequest request, User user) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "크루를 찾을 수 없습니다."));

        if (!crew.canEdit(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "크루를 수정할 권한이 없습니다.");
        }

        if (request.getTitle() != null) {
            crew.updateTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            crew.updateDescription(request.getDescription());
        }
        if (request.getMaxParticipants() != null) {
            crew.updateMaxParticipants(request.getMaxParticipants());
        }
        if (request.getWaypoints() != null) {
            crew.updateWaypoints(request.getWaypoints());
        }
        if (request.getTags() != null) {
            crew.updateTags(request.getTags());
        }
        if (request.getStartLocation() != null) {
            crew.updateStartLocation(request.getStartLocation());
        }
        if (request.getPace() != null) {
            crew.updatePace(request.getPace());
        }
        if (request.getStartTime() != null) {
            crew.updateStartTime(request.getStartTime());
        }

        crewRepository.save(crew);

        return CrewUpdateResponse.builder()
                .id(crew.getId())
                .status(crew.getStatus())
                .updatedAt(crew.getModifiedAt())
                .build();
    }

    public void deleteCrew(Long crewId, User user) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "크루를 찾을 수 없습니다."));

        if (!crew.canDelete(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "크루를 삭제할 권한이 없습니다.");
        }

        crewRepository.delete(crew);
    }

    public CrewApplyResponse applyToCrew(Long crewId, User user) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "크루를 찾을 수 없습니다."));

        if (crew.getStatus().name().equals("CLOSED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 모집이 완료된 크루입니다.");
        }

        if (crewParticipantRepository.existsByCrewAndUser(crew, user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 신청한 크루입니다.");
        }

        if (crew.getHost().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "호스트는 자신의 크루에 신청할 수 없습니다.");
        }

        CrewParticipant participant = CrewParticipant.builder()
                .crew(crew)
                .user(user)
                .status(ParticipantStatus.APPLIED)
                .build();

        crewParticipantRepository.save(participant);

        return CrewApplyResponse.builder()
                .status(ParticipantStatus.APPLIED)
                .build();
    }

    public CrewApprovalResponse approveParticipant(Long crewId, Long userId, ParticipantApprovalRequest request, User host) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "크루를 찾을 수 없습니다."));

        if (!crew.isHost(host) && !host.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "참여자를 승인할 권한이 없습니다.");
        }

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        CrewParticipant participant = crewParticipantRepository.findByCrewAndUser(crew, targetUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "참여 신청을 찾을 수 없습니다."));

        if (request.getApprove()) {
            participant.approve();
        } else {
            participant.reject();
        }

        crewParticipantRepository.save(participant);

        return CrewApprovalResponse.builder()
                .status(participant.getStatus())
                .build();
    }

    public Map<String, Object> getRouteInfo(String routeId) {
        try {
            // AI 모델 서비스 URL
            String aiServiceUrl = "http://43.202.57.158:5000";
            String url = aiServiceUrl + "/api/routes/recommend";
            
            // 기본 달서구 좌표와 설정값 사용
            Map<String, Object> requestBody = Map.of(
                "start_point", List.of(35.8667, 128.6000), // 달서구 중심 좌표
                "distance_km", 5.0, // 기본 5km
                "pace_min_per_km", 6.0 // 기본 6분/km
            );
            
            log.info("AI 모델 호출 시작: {}", url);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            
            if (response != null && response.containsKey("route")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> selectedRoute = (Map<String, Object>) response.get("route");
                
                // AI 모델 응답을 기존 형식으로 변환
                return Map.of(
                    "route_id", routeId,
                    "type", selectedRoute.getOrDefault("type", "safe"),
                    "distance_km", selectedRoute.getOrDefault("distance_km", 5.0),
                    "safety_score", ((Double) selectedRoute.getOrDefault("safety_score", 20.0)).intValue(),
                    "duration_min", ((Double) selectedRoute.getOrDefault("estimated_time_min", 30.0)).intValue(),
                    "waypoints", selectedRoute.getOrDefault("waypoints", List.of(List.of(35.8667, 128.6000)))
                );
            } else if (response != null && response.containsKey("routes")) {
                // 기존 형식도 지원 (fallback)
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                
                if (!routes.isEmpty()) {
                    Map<String, Object> selectedRoute = routes.get(0);
                    
                    return Map.of(
                        "route_id", routeId,
                        "type", selectedRoute.getOrDefault("type", "safe"),
                        "distance_km", selectedRoute.getOrDefault("distance_km", 5.0),
                        "safety_score", ((Double) selectedRoute.getOrDefault("safety_score", 20.0)).intValue(),
                        "duration_min", ((Double) selectedRoute.getOrDefault("estimated_time_min", 30.0)).intValue(),
                        "waypoints", selectedRoute.getOrDefault("waypoints", List.of(List.of(35.8667, 128.6000)))
                    );
                }
            }
            
            log.warn("AI 모델 응답이 예상 형식이 아님: {}", response);
            throw new BusinessException(ExceptionType.CREW_AI_SERVICE_ERROR, "AI 모델 응답 형식 오류");
            
        } catch (BusinessException e) {
            throw e; // BusinessException은 그대로 전파
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("AI 모델 HTTP 클라이언트 에러 ({}): {}", e.getStatusCode(), e.getMessage());
            log.info("AI 모델 연결 실패, 더미 데이터 반환");
            return getDummyRouteInfo(routeId);
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("AI 모델 HTTP 서버 에러 ({}): {}", e.getStatusCode(), e.getMessage());
            log.info("AI 모델 연결 실패, 더미 데이터 반환");
            return getDummyRouteInfo(routeId);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("AI 모델 리소스 접근 에러: {}", e.getMessage());
            log.info("AI 모델 연결 실패, 더미 데이터 반환");
            return getDummyRouteInfo(routeId);
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("AI 모델 RestClient 에러: {}", e.getMessage());
            log.info("AI 모델 연결 실패, 더미 데이터 반환");
            return getDummyRouteInfo(routeId);
        } catch (Exception e) {
            log.error("AI 모델 라우트 정보 조회 실패: {}", e.getMessage(), e);
            log.info("AI 모델 연결 실패, 더미 데이터 반환");
            return getDummyRouteInfo(routeId);
        }
    }
    
    private Map<String, Object> getDummyRouteInfo(String routeId) {
        // AI 모델 연결 실패 시 더미 데이터 반환
        return Map.of(
            "route_id", routeId,
            "type", "safe",
            "distance_km", 5.0,
            "safety_score", 20,
            "duration_min", 30,
            "waypoints", List.of(
                List.of(35.8667, 128.6000),
                List.of(35.8700, 128.6100),
                List.of(35.8750, 128.6200),
                List.of(35.8800, 128.6300),
                List.of(35.8667, 128.6000)
            )
        );
    }

    // 크루 리스트 조회 (검색 및 필터링)
    @Transactional(readOnly = true)
    public CrewListPageResponse searchCrews(CrewSearchRequest request) {
        // 정렬 설정
        Sort sort = createSort(request);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // 검색 조건 변환
        CrewStatus status = request.getStatus() != null ? 
            CrewStatus.valueOf(request.getStatus().toUpperCase()) : null;
        SafetyLevel safetyLevel = request.getSafetyLevel() != null ? 
            SafetyLevel.valueOf(request.getSafetyLevel().toUpperCase()) : null;
        
        Page<Crew> crewPage = crewRepository.searchCrews(
            request.getKeyword(),
            request.getStartLocation(),
            status,
            safetyLevel,
            request.getTags(),
            request.getMaxDistance(),
            request.getMinPace(),
            request.getStartTimeFrom(),
            pageable
        );
        
        List<CrewListResponse> crewList = crewPage.getContent().stream()
            .map(this::convertToCrewListResponse)
            .collect(Collectors.toList());
        
        return CrewListPageResponse.builder()
            .crews(crewList)
            .currentPage(crewPage.getNumber())
            .totalPages(crewPage.getTotalPages())
            .totalElements(crewPage.getTotalElements())
            .size(crewPage.getSize())
            .hasNext(crewPage.hasNext())
            .hasPrevious(crewPage.hasPrevious())
            .isFirst(crewPage.isFirst())
            .isLast(crewPage.isLast())
            .build();
    }
    

    
    // 크루 상세 조회
    @Transactional(readOnly = true)
    public CrewDetailResponse getCrewDetail(Long crewId) {
        Crew crew = crewRepository.findById(crewId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "크루를 찾을 수 없습니다."));
        
        Long currentParticipants = crewRepository.countApprovedParticipants(crew);
        
        List<CrewDetailResponse.ParticipantInfo> participants = crew.getParticipants().stream()
            .map(participant -> CrewDetailResponse.ParticipantInfo.builder()
                .userId(participant.getUser().getId())
                .userName(participant.getUser().getUsername())
                .userEmail(participant.getUser().getEmail())
                .status(participant.getStatus().name())
                .appliedAt(participant.getCreatedAt())
                .build())
            .collect(Collectors.toList());
        
        return CrewDetailResponse.builder()
            .id(crew.getId())
            .title(crew.getTitle())
            .description(crew.getDescription())
            .status(crew.getStatus())
            .hostName(crew.getHost().getUsername())
            .hostEmail(crew.getHost().getEmail())
            .maxParticipants(crew.getMaxParticipants())
            .currentParticipants(currentParticipants.intValue())
            .routeId(crew.getRouteId())
            .type(crew.getType())
            .distanceKm(crew.getDistanceKm())
            .safetyScore(crew.getSafetyScore())
            .safetyLevel(crew.getSafetyLevel())
            .durationMin(crew.getDurationMin())
            .waypoints(crew.getWaypoints())
            .tags(crew.getTags())
            .startLocation(crew.getStartLocation())
            .pace(crew.getPace())
            .startTime(crew.getStartTime())
            .participants(participants)
            .createdAt(crew.getCreatedAt())
            .updatedAt(crew.getModifiedAt())
            .build();
    }
    
    // Crew를 CrewListResponse로 변환
    private CrewListResponse convertToCrewListResponse(Crew crew) {
        Long currentParticipants = crewRepository.countApprovedParticipants(crew);
        
        return CrewListResponse.builder()
            .id(crew.getId())
            .title(crew.getTitle())
            .description(crew.getDescription())
            .status(crew.getStatus())
            .hostName(crew.getHost().getUsername())
            .maxParticipants(crew.getMaxParticipants())
            .currentParticipants(currentParticipants.intValue())
            .routeId(crew.getRouteId())
            .type(crew.getType())
            .distanceKm(crew.getDistanceKm())
            .safetyScore(crew.getSafetyScore())
            .safetyLevel(crew.getSafetyLevel())
            .durationMin(crew.getDurationMin())
            .waypoints(crew.getWaypoints())
            .tags(crew.getTags())
            .startLocation(crew.getStartLocation())
            .pace(crew.getPace())
            .startTime(crew.getStartTime())
            .createdAt(crew.getCreatedAt())
            .updatedAt(crew.getModifiedAt())
            .build();
    }
    
    // 정렬 설정 생성
    private Sort createSort(CrewSearchRequest request) {
        String sortType = request.getSortType();
        String sortDirection = request.getSortDirection();
        
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        
        if ("popular".equalsIgnoreCase(sortType)) {
            // 인기순: 참여자 수 기준 (내림차순)
            return Sort.by(Sort.Direction.DESC, "createdAt"); // 임시로 createdAt 사용
        } else if ("latest".equalsIgnoreCase(sortType)) {
            // 최신순: 생성일 기준 (내림차순)
            return Sort.by(Sort.Direction.DESC, "createdAt");
        } else if ("distance".equalsIgnoreCase(sortType)) {
            // 거리순: 거리 기준
            return Sort.by(direction, "distanceKm");
        } else if ("pace".equalsIgnoreCase(sortType)) {
            // 페이스순: 페이스 기준
            return Sort.by(direction, "pace");
        } else if ("time".equalsIgnoreCase(sortType)) {
            // 시작시간순: 시작시간 기준
            return Sort.by(direction, "startTime");
        } else {
            // 기본: 생성일 기준
            return Sort.by(direction, request.getSortBy());
        }
    }
}
