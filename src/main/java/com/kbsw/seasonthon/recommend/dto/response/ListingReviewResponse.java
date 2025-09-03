package com.kbsw.seasonthon.recommend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ListingReviewResponse {

    @Schema(description = "날씨,온도,습도,미세먼,강수확률을 고려한 조언")
    private String advice;


    @Schema(description = "LLM 사용 여부(폴백이면 false)")
    private boolean fromLlm;



}