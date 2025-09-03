package com.kbsw.seasonthon.recommend.controller;

import com.kbsw.seasonthon.recommend.dto.request.LiveAdviceRequest;
import com.kbsw.seasonthon.recommend.dto.request.ListingReviewRequest;
import com.kbsw.seasonthon.recommend.dto.response.ListingReviewResponse;
import com.kbsw.seasonthon.recommend.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recommend", description = "AI 가격/날씨 조언 API")
@RequestMapping(value = "/api/recommend", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping("/review")
    @Operation(
            summary = "요청 바디의 날씨값으로 조언",
            description = "리퀘스트에 담긴 날씨 데이터로 한 줄 조언을 반환합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ListingReviewRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "맑고 더운 날",
                                            value = """
                        {
                          "Weather": 101,
                          "Temperature": 31.0,
                          "Dust": 40,
                          "Humidity": 55,
                          "Rainfall": 10.0
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "비 가능성 높음",
                                            value = """
                        {
                          "Weather": 61,
                          "Temperature": 22.3,
                          "Dust": 25,
                          "Humidity": 85,
                          "Rainfall": 70.0
                        }
                        """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공",
                            content = @Content(
                                    schema = @Schema(implementation = ListingReviewResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "advice": "더운 날씨이니 수분을 충분히 섭취하세요.",
                          "fromLlm": true
                        }
                        """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ListingReviewResponse> listingReview(
            @Valid @RequestBody ListingReviewRequest request
    ) {
        return ResponseEntity.ok(recommendService.listingReview(request));
    }

    @PostMapping("/review/live")
    @Operation(
            summary = "좌표 기준 실시간 조언",
            description = "위도/경도로 실시간 날씨를 조회해 한 줄 조언을 반환합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LiveAdviceRequest.class),
                            examples = @ExampleObject(
                                    name = "서울 시청",
                                    value = """
                    {
                      "lat": 37.5665,
                      "lon": 126.9780
                    }
                    """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공",
                            content = @Content(
                                    schema = @Schema(implementation = ListingReviewResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "advice": "오후 소나기가 올 수 있어요. 가벼운 우산을 챙기세요.",
                          "fromLlm": true
                        }
                        """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ListingReviewResponse> liveListingReview(
            @Valid @RequestBody LiveAdviceRequest request
    ) {
        return ResponseEntity.ok(recommendService.liveListingReview(request));
    }
}