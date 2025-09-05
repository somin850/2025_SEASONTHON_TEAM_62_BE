package com.kbsw.seasonthon.crew.controller;

import com.kbsw.seasonthon.crew.dto.request.CrewCreateRequest;
import com.kbsw.seasonthon.crew.dto.request.CrewSearchRequest;
import com.kbsw.seasonthon.crew.dto.request.CrewUpdateRequest;
import com.kbsw.seasonthon.crew.dto.request.ParticipantApprovalRequest;
import com.kbsw.seasonthon.crew.dto.response.CrewApplyResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewApprovalResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewCreateResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewDetailResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewListPageResponse;
import com.kbsw.seasonthon.crew.dto.response.CrewUpdateResponse;
import com.kbsw.seasonthon.crew.service.CrewService;
import com.kbsw.seasonthon.security.oauth2.principal.PrincipalDetails;
import com.kbsw.seasonthon.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crews")
@RequiredArgsConstructor
@Tag(name = "Crew", description = "크루 관련 API")
public class CrewController {

    private final CrewService crewService;

    @PostMapping
    @Operation(summary = "크루 생성", description = "새로운 크루를 생성합니다.")
    public ResponseEntity<CrewCreateResponse> createCrew(
            @Valid @RequestBody CrewCreateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        // 개발 환경에서는 더미 사용자 사용
        User user;
        if (principal != null) {
            user = principal.getUser();
        } else {
            // 더미 사용자 생성 또는 조회
            user = getDummyUser();
        }
        
        CrewCreateResponse response = crewService.createCrew(request, user);
        
        return ResponseEntity.ok(response);
    }
    
    private User getDummyUser() {
        // 더미 사용자 반환 (실제 구현에서는 UserRepository에서 조회)
        return User.builder()
                .id(1L)
                .username("dummy_user")
                .email("dummy@example.com")
                .build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "크루 수정", description = "크루 정보를 부분적으로 수정합니다. (작성자/관리자만 가능)")
    public ResponseEntity<CrewUpdateResponse> updateCrew(
            @PathVariable Long id,
            @Valid @RequestBody CrewUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        User user = principal != null ? principal.getUser() : getDummyUser();
        CrewUpdateResponse response = crewService.updateCrew(id, request, user);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "크루 삭제", description = "크루를 삭제합니다. (작성자/관리자만 가능)")
    public ResponseEntity<Void> deleteCrew(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        User user = principal != null ? principal.getUser() : getDummyUser();
        crewService.deleteCrew(id, user);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/apply")
    @Operation(summary = "크루 참여 신청", description = "크루에 참여를 신청합니다.")
    public ResponseEntity<CrewApplyResponse> applyToCrew(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        User user = principal != null ? principal.getUser() : getDummyUser();
        CrewApplyResponse response = crewService.applyToCrew(id, user);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/participants/{userId}/approve")
    @Operation(summary = "크루 참여 승인", description = "크루 참여 신청을 승인하거나 거절합니다. (호스트/관리자만 가능)")
    public ResponseEntity<CrewApprovalResponse> approveParticipant(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody ParticipantApprovalRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        User user = principal != null ? principal.getUser() : getDummyUser();
        CrewApprovalResponse response = crewService.approveParticipant(id, userId, request, user);
        
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    @Operation(summary = "크루 상세 조회", description = "특정 크루의 상세 정보를 조회합니다.")
    public ResponseEntity<CrewDetailResponse> getCrewDetail(@PathVariable Long id) {
        
        CrewDetailResponse response = crewService.getCrewDetail(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(summary = "전체 크루 리스트 조회", description = "모든 크루를 페이징하여 조회합니다. 필터링 없이 전체 크루를 볼 수 있습니다.")
    public ResponseEntity<CrewListPageResponse> getAllCrews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        CrewSearchRequest request = new CrewSearchRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        
        CrewListPageResponse response = crewService.searchCrews(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "크루 리스트 조회 (검색 + 필터링)", 
        description = "검색 조건에 따라 크루 리스트를 조회합니다.\n\n" +
                     "**검색 필드 (조회용):**\n" +
                     "- keyword: 제목, 설명 키워드 검색\n" +
                     "- startLocation: 시작 위치 검색\n" +
                     "- tags: 태그 검색 (여러 개 가능)\n" +
                     "- status: 크루 상태 검색\n" +
                     "- safetyLevel: 안전도 검색\n\n" +
                     "**필터링 필드 (실제 필터링 동작):**\n" +
                     "- maxDistance: 최대 거리 필터 (km) - 이 거리 이하의 크루들\n" +
                     "- minPace: 최소 페이스 필터 - 이 페이스보다 빠르거나 같은 크루들\n" +
                     "- startTimeFrom: 시작 시간 이후 필터 - 이 시간 이후에 시작하는 크루들\n\n" +
                     "**배열 파라미터 사용법:**\n" +
                     "- tags: `?tags=친화적인&tags=러닝&tags=초보환영`\n\n" +
                     "**예시:**\n" +
                     "- 전체 조회: `/api/crews`\n" +
                     "- 키워드 검색: `/api/crews?keyword=취준생`\n" +
                     "- 실제 필터링: `/api/crews?maxDistance=10.0&minPace=6'00\"/km`"
    )
    public ResponseEntity<CrewListPageResponse> searchCrews(
            @ModelAttribute CrewSearchRequest request) {
        
        CrewListPageResponse response = crewService.searchCrews(request);
        return ResponseEntity.ok(response);
    }
}
