package com.kbsw.seasonthon.report.controller;

import com.kbsw.seasonthon.global.base.response.ResponseBody;
import com.kbsw.seasonthon.global.base.response.ResponseUtil;
import com.kbsw.seasonthon.report.dto.request.ReportCreateRequest;
import com.kbsw.seasonthon.report.dto.request.ReportStatusUpdateRequest;
import com.kbsw.seasonthon.report.dto.response.ReportListResponse;
import com.kbsw.seasonthon.report.dto.response.ReportResponse;
import com.kbsw.seasonthon.report.service.ReportService;
import com.kbsw.seasonthon.security.oauth2.principal.PrincipalDetails;
import com.kbsw.seasonthon.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hazards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report API", description = "신고 관련 API")
public class ReportController {

    private final ReportService reportService;
    
    /**
     * 테스트 엔드포인트
     */
    @GetMapping("/test")
    @Operation(summary = "테스트", description = "ReportController 테스트")
    public ResponseEntity<ResponseBody<String>> test() {
        try {
            return ResponseEntity.ok(ResponseUtil.createSuccessResponse("ReportController is working!"));
        } catch (Exception e) {
            log.error("ReportController 테스트 오류: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResponseUtil.createErrorResponse("C001", "ReportController 오류: " + e.getMessage()));
        }
    }

    /**
     * 신고 생성
     * POST /hazards
     */
    @PostMapping
    @Operation(summary = "신고 생성", description = "새로운 위험 요소를 신고합니다.")
    public ResponseEntity<ResponseBody<ReportResponse>> createReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "신고 생성 요청 정보", required = true,
                    content = @Content(schema = @Schema(implementation = ReportCreateRequest.class))
            )
            @Valid @RequestBody ReportCreateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        if (principal == null || principal.getUser() == null) {
            log.warn("인증되지 않은 신고 생성 요청");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        log.info("신고 생성 API 호출 - 신고자: {}, 대상타입: {}", 
                principal.getUser().getId(), request.getTargetType());
        
        User user = principal.getUser();
        ReportResponse response = reportService.createReport(request, user);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.createSuccessResponse(response));
    }

    /**
     * 목록(검색)
     * GET /hazards/all
     */
    @GetMapping("/all")
    @Operation(summary = "전체 신고 목록 조회", description = "모든 신고 목록을 조회합니다. (누구나 조회 가능)")
    public ResponseEntity<ResponseBody<List<ReportListResponse>>> getAllReports(
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        String userId = (principal != null && principal.getUser() != null) 
            ? principal.getUser().getId().toString() 
            : "익명사용자";
            
        log.info("전체 신고 목록 조회 API 호출 - 사용자: {}", userId);
        
        List<ReportListResponse> response = reportService.getAllReports();
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /**
     * 상세
     * GET /hazards/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "신고 상세 조회", description = "특정 신고의 상세 정보를 조회합니다. (누구나 조회 가능)")
    public ResponseEntity<ResponseBody<ReportResponse>> getReport(
            @Parameter(description = "신고 ID", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        String userId = (principal != null && principal.getUser() != null) 
            ? principal.getUser().getId().toString() 
            : "익명사용자";
            
        log.info("신고 상세 조회 API 호출 - 신고ID: {}, 사용자: {}", id, userId);
        
        User user = (principal != null) ? principal.getUser() : null;
        ReportResponse response = reportService.getReport(id, user);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /**
     * 상태 변경
     * PATCH /hazards/{id}/status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "신고 상태 변경", description = "신고의 처리 상태를 변경합니다. (관리자 전용)")
    public ResponseEntity<ResponseBody<ReportResponse>> updateReportStatus(
            @Parameter(description = "신고 ID", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "상태 변경 요청 정보", required = true,
                    content = @Content(schema = @Schema(implementation = ReportStatusUpdateRequest.class))
            )
            @Valid @RequestBody ReportStatusUpdateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        if (principal == null || principal.getUser() == null) {
            log.warn("인증되지 않은 신고 상태 변경 요청 - 신고ID: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        log.info("신고 상태 변경 API 호출 - 신고ID: {}, 새 상태: {}, 관리자: {}", 
                id, request.getStatus(), principal.getUser().getId());
        
        User admin = principal.getUser();
        ReportResponse response = reportService.updateReportStatus(id, request, admin);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /**
     * 내 신고
     * GET /hazards/me
     */
    @GetMapping("/me")
    @Operation(summary = "내 신고 목록 조회", description = "현재 사용자가 신고한 목록을 조회합니다.")
    public ResponseEntity<ResponseBody<List<ReportListResponse>>> getMyReports(
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        if (principal == null || principal.getUser() == null) {
            log.warn("인증되지 않은 내 신고 목록 조회 요청");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        log.info("내 신고 목록 조회 API 호출 - 사용자: {}", principal.getUser().getId());
        
        User user = principal.getUser();
        List<ReportListResponse> response = reportService.getMyReports(user);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /**
     * 삭제
     * DELETE /hazards/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "신고 삭제", description = "신고를 삭제합니다. 본인의 미처리 신고만 삭제 가능합니다.")
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "신고 ID", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principal) {
        
        if (principal == null || principal.getUser() == null) {
            log.warn("인증되지 않은 신고 삭제 요청 - 신고ID: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        log.info("신고 삭제 API 호출 - 신고ID: {}, 사용자: {}", id, principal.getUser().getId());
        
        User user = principal.getUser();
        reportService.deleteReport(id, user);
        
        return ResponseEntity.noContent().build();
    }
}
