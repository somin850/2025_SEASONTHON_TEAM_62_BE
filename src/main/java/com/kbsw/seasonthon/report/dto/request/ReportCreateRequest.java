package com.kbsw.seasonthon.report.dto.request;

import com.kbsw.seasonthon.report.enums.TargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCreateRequest {

    @Schema(description = "신고 대상 타입", example = "ROUTE")
    @NotNull(message = "신고 대상 타입은 필수입니다.")
    private TargetType targetType;

    @Schema(description = "신고 대상 ID", example = "123")
    @NotNull(message = "신고 대상 ID는 필수입니다.")
    private Long targetId;

    @Schema(description = "신고 사유", example = "이 경로에 공사 중인 구간이 있어 위험합니다.")
    @NotBlank(message = "신고 사유는 필수입니다.")
    private String reason;
}
