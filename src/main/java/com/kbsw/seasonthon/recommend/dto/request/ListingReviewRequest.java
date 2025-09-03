package com.kbsw.seasonthon.recommend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ListingReviewRequest {

    @Schema(description = "날씨", example = "101")
    private Long Weather;

    @Schema(description = "기온", example = "25.5")
    private Double Temperature;

    @Schema(description = "미세먼지", example = "45")
    private Long Dust;

    @Schema(description = "습도", example = "60")
    private Long Humidity;

    @Schema(description = "강수 확률", example = "0")
    private Double Rainfall;



}