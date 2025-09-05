package com.kbsw.seasonthon.report.dto.request;

import com.kbsw.seasonthon.report.enums.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportStatusUpdateRequest {

    @Schema(description = "변경할 신고 상태", example = "RESOLVED")
    @NotNull(message = "신고 상태는 필수입니다.")
    private ReportStatus status;
}
