package com.kbsw.seasonthon.recommend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LiveAdviceRequest {
    @Schema(example = "37.5665") // 위도
    private double lat;
    @Schema(example = "126.9780") // 경도
    private double lon;
}