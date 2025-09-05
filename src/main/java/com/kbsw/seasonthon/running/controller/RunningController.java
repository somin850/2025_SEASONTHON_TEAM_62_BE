package com.kbsw.seasonthon.running.controller;

import com.kbsw.seasonthon.global.base.response.ResponseBody;
import com.kbsw.seasonthon.global.base.response.ResponseUtil;
import com.kbsw.seasonthon.report.dto.request.ReportCreateRequest;
import com.kbsw.seasonthon.report.dto.request.ReportStatusUpdateRequest;
import com.kbsw.seasonthon.report.dto.response.ReportListResponse;
import com.kbsw.seasonthon.report.dto.response.ReportResponse;
import com.kbsw.seasonthon.report.service.ReportService;
import com.kbsw.seasonthon.running.dto.response.RunningStatsResponse;
import com.kbsw.seasonthon.running.service.RunningRecordService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/running")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Running API", description = "러닝 기록 및 통계 관련 API")
public class RunningController {

    private final ReportService reportService;
    private final RunningRecordService runningRecordService;
    
    /**
     * 테스트 엔드포인트
     */
    @GetMapping("/test")
    @Operation(summary = "테스트", description = "RunningController 테스트")
    public ResponseEntity<ResponseBody<String>> test() {
        try {
            return ResponseEntity.ok(ResponseUtil.createSuccessResponse("RunningController is working!"));
        } catch (Exception e) {
            log.error("RunningController 테스트 오류: {}", e.getMessage(), e);
            return ResponseEntity.ok(ResponseUtil.createSuccessResponse("RunningController 오류: " + e.getMessage()));
        }
    }

    /**
     * 러닝 통계 조회
     */
    @GetMapping("/stats")
    @Operation(summary = "러닝 통계 조회", description = "현재 사용자의 러닝 통계를 조회합니다. (개발 모드: 더미 데이터)")
    public ResponseEntity<ResponseBody<RunningStatsResponse>> getRunningStats() {
        
        log.info("러닝 통계 조회 API 호출 - 개발 모드: 더미 사용자 사용");
        
        // 개발 모드: 더미 사용자 사용
        User dummyUser = getDummyUser();
        RunningStatsResponse stats = runningRecordService.getRunningStats(dummyUser);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(stats));
    }
    
    private User getDummyUser() {
        // 더미 사용자 반환 (실제 구현에서는 UserRepository에서 조회)
        return User.builder()
                .id(4L)  // admin 사용자 ID
                .username("admin")
                .email("admin@example.com")
                .build();
    }

    /**
     * 신고 생성
     * POST /hazards
     */
    @PostMapping
    @Operation(summary = "신고 생성", description = "새로운 위험 요소를 신고합니다. (개발 모드: 더미 사용자)")
    public ResponseEntity<ResponseBody<ReportResponse>> createReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "신고 생성 요청 정보", required = true,
                    content = @Content(schema = @Schema(implementation = ReportCreateRequest.class))
            )
            @Valid @RequestBody ReportCreateRequest request) {
        
        log.info("신고 생성 API 호출 - 개발 모드: 더미 사용자 사용, 대상타입: {}", request.getTargetType());
        
        User user = getDummyUser();
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
    public ResponseEntity<ResponseBody<List<ReportListResponse>>> getAllReports() {
            
        log.info("전체 신고 목록 조회 API 호출 - 개발 모드: 인증 없이 조회");
        
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
            @PathVariable Long id) {
            
        log.info("신고 상세 조회 API 호출 - 신고ID: {}, 개발 모드: 인증 없이 조회", id);
        
        ReportResponse response = reportService.getReport(id, null);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /**
     * 상태 변경
     * PATCH /hazards/{id}/status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "신고 상태 변경", description = "신고의 처리 상태를 변경합니다. (개발 모드: 더미 관리자)")
    public ResponseEntity<ResponseBody<ReportResponse>> updateReportStatus(
            @Parameter(description = "신고 ID", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "상태 변경 요청 정보", required = true,
                    content = @Content(schema = @Schema(implementation = ReportStatusUpdateRequest.class))
            )
            @Valid @RequestBody ReportStatusUpdateRequest request) {
        
        log.info("신고 상태 변경 API 호출 - 신고ID: {}, 새 상태: {}, 개발 모드: 더미 관리자 사용", 
                id, request.getStatus());
        
        User admin = getDummyUser();
        ReportResponse response = reportService.updateReportStatus(id, request, admin);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /**
     * 내 신고
     * GET /hazards/me
     */
    @GetMapping("/me")
    @Operation(summary = "내 신고 목록 조회", description = "현재 사용자가 신고한 목록을 조회합니다. (개발 모드: 더미 사용자)")
    public ResponseEntity<ResponseBody<List<ReportListResponse>>> getMyReports() {
        
        log.info("내 신고 목록 조회 API 호출 - 개발 모드: 더미 사용자 사용");
        
        User user = getDummyUser();
        List<ReportListResponse> response = reportService.getMyReports(user);
        
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    /**
     * 삭제
     * DELETE /hazards/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "신고 삭제", description = "신고를 삭제합니다. (개발 모드: 더미 사용자)")
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "신고 ID", required = true)
            @PathVariable Long id) {
        
        log.info("신고 삭제 API 호출 - 신고ID: {}, 개발 모드: 더미 사용자 사용", id);
        
        User user = getDummyUser();
        reportService.deleteReport(id, user);
        
        return ResponseEntity.noContent().build();
    }
}
