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
public class ReportResponse {

    @Schema(description = "신고 ID", example = "1")
    private Long id;

    @Schema(description = "신고 대상 타입", example = "ROUTE")
    private TargetType targetType;

    @Schema(description = "신고 대상 ID", example = "123")
    private Long targetId;

    @Schema(description = "신고자 ID", example = "456")
    private Long reporterId;

    @Schema(description = "신고자 닉네임", example = "user123")
    private String reporterNickname;

    @Schema(description = "신고 사유", example = "이 경로에 공사 중인 구간이 있어 위험합니다.")
    private String reason;

    @Schema(description = "신고 상태", example = "OPEN")
    private ReportStatus status;

    @Schema(description = "신고 생성 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "신고 수정 시간", example = "2024-01-15T14:20:00")
    private LocalDateTime modifiedAt;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reporterId(report.getReporter().getId())
                .reporterNickname(report.getReporter().getNickname())
                .reason(report.getReason())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .modifiedAt(report.getModifiedAt())
                .build();
    }
}
