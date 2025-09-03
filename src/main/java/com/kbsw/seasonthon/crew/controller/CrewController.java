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
        
        User user = principal.getUser();
        CrewCreateResponse response = crewService.createCrew(request, user);
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "크루 수정", description = "크루 정보를 부분적으로 수정합니다. (작성자/관리자만 가능)")
    public ResponseEntity<CrewUpdateResponse> updateCrew(
            @PathVariable Long id,
            @Valid @RequestBody CrewUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        User user = principal.getUser();
        CrewUpdateResponse response = crewService.updateCrew(id, request, user);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "크루 삭제", description = "크루를 삭제합니다. (작성자/관리자만 가능)")
    public ResponseEntity<Void> deleteCrew(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        User user = principal.getUser();
        crewService.deleteCrew(id, user);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/apply")
    @Operation(summary = "크루 참여 신청", description = "크루에 참여를 신청합니다.")
    public ResponseEntity<CrewApplyResponse> applyToCrew(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        User user = principal.getUser();
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
        
        User user = principal.getUser();
        CrewApprovalResponse response = crewService.approveParticipant(id, userId, request, user);
        
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    @Operation(summary = "크루 상세 조회", description = "특정 크루의 상세 정보를 조회합니다.")
    public ResponseEntity<CrewDetailResponse> getCrewDetail(@PathVariable Long id) {
        
        CrewDetailResponse response = crewService.getCrewDetail(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "크루 리스트 조회", description = "검색 조건에 따라 크루 리스트를 조회합니다.")
    public ResponseEntity<CrewListPageResponse> searchCrews(
            @ModelAttribute CrewSearchRequest request) {
        
        CrewListPageResponse response = crewService.searchCrews(request);
        return ResponseEntity.ok(response);
    }
}
