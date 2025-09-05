package com.kbsw.seasonthon.report.dto.response;

import com.kbsw.seasonthon.report.entity.Report;
import com.kbsw.seasonthon.report.enums.ReportStatus;
import com.kbsw.seasonthon.report.enums.TargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportListResponse {

    @Schema(description = "신고 ID", example = "1")
    private Long id;

    @Schema(description = "신고 대상 타입", example = "ROUTE")
    private TargetType targetType;

    @Schema(description = "신고 대상 ID", example = "123")
    private Long targetId;

    @Schema(description = "신고자 닉네임", example = "user123")
    private String reporterNickname;

    @Schema(description = "신고 사유 (요약)", example = "이 경로에 공사 중인 구간이...")
    private String reasonSummary;

    @Schema(description = "신고 상태", example = "OPEN")
    private ReportStatus status;

    @Schema(description = "신고 생성 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    public static ReportListResponse from(Report report) {
        String reasonSummary = report.getReason().length() > 50 
            ? report.getReason().substring(0, 50) + "..." 
            : report.getReason();
            
        return ReportListResponse.builder()
                .id(report.getId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reporterNickname(report.getReporter().getNickname())
                .reasonSummary(reasonSummary)
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
