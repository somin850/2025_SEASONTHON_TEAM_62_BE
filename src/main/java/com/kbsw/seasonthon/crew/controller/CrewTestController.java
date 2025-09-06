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
import com.kbsw.seasonthon.crew.service.CrewTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/crews")
@RequiredArgsConstructor
@Tag(name = "Crew Test", description = "크루 테스트 API (인증 없음)")
public class CrewTestController {

    private final CrewTestService crewTestService;

    @PostMapping
    @Operation(summary = "크루 생성 (테스트)", description = "새로운 크루를 생성합니다. (인증 없음)")
    public ResponseEntity<CrewCreateResponse> createCrew(
            @Valid @RequestBody CrewCreateRequest request) {
        
        CrewCreateResponse response = crewTestService.createCrew(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "크루 수정 (테스트)", description = "크루 정보를 부분적으로 수정합니다. (인증 없음)")
    public ResponseEntity<CrewUpdateResponse> updateCrew(
            @PathVariable Long id,
            @Valid @RequestBody CrewUpdateRequest request) {
        
        CrewUpdateResponse response = crewTestService.updateCrew(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "크루 삭제 (테스트)", description = "크루를 삭제합니다. (인증 없음)")
    public ResponseEntity<Void> deleteCrew(@PathVariable Long id) {
        crewTestService.deleteCrew(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/apply")
    @Operation(summary = "크루 참여 신청 (테스트)", description = "크루에 참여를 신청합니다. (인증 없음)")
    public ResponseEntity<CrewApplyResponse> applyToCrew(@PathVariable Long id) {
        CrewApplyResponse response = crewTestService.applyToCrew(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/participants/{userId}/approve")
    @Operation(summary = "크루 참여 승인 (테스트)", description = "크루 참여 신청을 승인하거나 거절합니다. (인증 없음)")
    public ResponseEntity<CrewApprovalResponse> approveParticipant(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody ParticipantApprovalRequest request) {
        
        CrewApprovalResponse response = crewTestService.approveParticipant(id, userId, request);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    @Operation(summary = "크루 상세 조회 (테스트)", description = "특정 크루의 상세 정보를 조회합니다. (인증 없음)")
    public ResponseEntity<CrewDetailResponse> getCrewDetail(@PathVariable Long id) {
        
        CrewDetailResponse response = crewTestService.getCrewDetail(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "크루 리스트 조회 (테스트)", description = "검색 조건에 따라 크루 리스트를 조회합니다. (인증 없음)")
    public ResponseEntity<CrewListPageResponse> searchCrews(
            @ModelAttribute CrewSearchRequest request) {
        
        CrewListPageResponse response = crewTestService.searchCrews(request);
        return ResponseEntity.ok(response);
    }
}


