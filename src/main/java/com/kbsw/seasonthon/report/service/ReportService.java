package com.kbsw.seasonthon.report.service;

import com.kbsw.seasonthon.global.base.response.exception.BusinessException;
import com.kbsw.seasonthon.global.base.response.exception.ExceptionType;
import com.kbsw.seasonthon.report.dto.request.ReportCreateRequest;
import com.kbsw.seasonthon.report.dto.request.ReportStatusUpdateRequest;
import com.kbsw.seasonthon.report.dto.response.ReportListResponse;
import com.kbsw.seasonthon.report.dto.response.ReportResponse;
import com.kbsw.seasonthon.report.entity.Report;
import com.kbsw.seasonthon.report.enums.ReportStatus;
import com.kbsw.seasonthon.report.repository.ReportRepository;
import com.kbsw.seasonthon.security.jwt.enums.Role;
import com.kbsw.seasonthon.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * 신고 생성
     */
    @Transactional
    public ReportResponse createReport(ReportCreateRequest request, User reporter) {
        log.info("신고 생성 요청 - 신고자: {}, 대상타입: {}, 대상ID: {}", 
                reporter.getId(), request.getTargetType(), request.getTargetId());

        Report report = Report.builder()
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reporter(reporter)
                .reason(request.getReason())
                .status(ReportStatus.OPEN)
                .build();

        Report savedReport = reportRepository.save(report);
        log.info("신고 생성 완료 - 신고ID: {}", savedReport.getId());

        return ReportResponse.from(savedReport);
    }

    /**
     * 모든 신고 목록 조회 (관리자용)
     */
    public List<ReportListResponse> getAllReports() {
        log.info("전체 신고 목록 조회");
        
        List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();
        return reports.stream()
                .map(ReportListResponse::from)
                .toList();
    }

    /**
     * 신고 상세 조회
     */
    public ReportResponse getReport(Long reportId, User user) {
        String userInfo = (user != null) ? user.getId().toString() : "익명사용자";
        log.info("신고 상세 조회 - 신고ID: {}, 사용자: {}", reportId, userInfo);
        
        Report report = findReportById(reportId);
        
        // 인증된 사용자인 경우에만 권한 체크 (익명 사용자는 모든 신고 조회 가능)
        if (user != null) {
            // 관리자가 아니고 본인의 신고가 아닌 경우에도 조회 허용 (공개 정보)
            // 필요시 여기서 추가 권한 체크 로직 구현 가능
        }
        
        return ReportResponse.from(report);
    }

    /**
     * 신고 상태 변경 (관리자용)
     */
    @Transactional
    public ReportResponse updateReportStatus(Long reportId, ReportStatusUpdateRequest request, User admin) {
        log.info("신고 상태 변경 - 신고ID: {}, 새 상태: {}, 관리자: {}", 
                reportId, request.getStatus(), admin.getId());

        // 관리자 권한 확인
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new BusinessException(ExceptionType.REPORT_ACCESS_DENIED);
        }

        Report report = findReportById(reportId);
        report.updateStatus(request.getStatus());

        log.info("신고 상태 변경 완료 - 신고ID: {}, 상태: {}", reportId, request.getStatus());
        return ReportResponse.from(report);
    }

    /**
     * 내 신고 목록 조회
     */
    public List<ReportListResponse> getMyReports(User user) {
        log.info("내 신고 목록 조회 - 사용자ID: {}", user.getId());
        
        List<Report> reports = reportRepository.findByReporterIdOrderByCreatedAtDesc(user.getId());
        return reports.stream()
                .map(ReportListResponse::from)
                .toList();
    }

    /**
     * 신고 삭제 (본인 신고만 삭제 가능, OPEN 상태일 때만)
     */
    @Transactional
    public void deleteReport(Long reportId, User user) {
        log.info("신고 삭제 - 신고ID: {}, 사용자ID: {}", reportId, user.getId());
        
        Report report = findReportById(reportId);
        
        // 본인의 신고가 아닌 경우 접근 거부
        if (!report.getReporter().getId().equals(user.getId())) {
            throw new BusinessException(ExceptionType.REPORT_ACCESS_DENIED);
        }
        
        // 이미 처리된 신고는 삭제 불가
        if (!report.getStatus().equals(ReportStatus.OPEN)) {
            throw new BusinessException(ExceptionType.REPORT_ALREADY_PROCESSED);
        }
        
        reportRepository.delete(report);
        log.info("신고 삭제 완료 - 신고ID: {}", reportId);
    }

    /**
     * 신고 ID로 신고 조회 (공통 메서드)
     */
    private Report findReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ExceptionType.REPORT_NOT_FOUND));
    }
}
