package com.kbsw.seasonthon.report.repository;

import com.kbsw.seasonthon.report.entity.Report;
import com.kbsw.seasonthon.report.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 특정 사용자의 신고 목록 조회
     */
    List<Report> findByReporterIdOrderByCreatedAtDesc(Long reporterId);

    /**
     * 모든 신고 목록 조회 (생성일 기준 내림차순)
     */
    List<Report> findAllByOrderByCreatedAtDesc();

    /**
     * 상태별 신고 목록 조회
     */
    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    /**
     * 특정 사용자의 특정 상태 신고 목록 조회
     */
    List<Report> findByReporterIdAndStatusOrderByCreatedAtDesc(Long reporterId, ReportStatus status);
}
