package com.kbsw.seasonthon.crew.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "크루 검색 요청")
public class CrewSearchRequest {
    
    // 조회용 필드들 (필터링 아님)
    @Schema(description = "제목, 설명 키워드 검색 (조회용)", example = "취준생")
    private String keyword;
    
    @Schema(description = "시작 위치 검색 (조회용)", example = "대전")
    private String startLocation;
    
    @Schema(
        description = "태그 검색 (조회용, 여러 개 가능)", 
        example = "[\"친화적인\", \"러닝\", \"초보환영\"]",
        implementation = String.class
    )
    private List<String> tags;
    
    @Schema(description = "크루 상태 검색 (조회용)", example = "OPEN", allowableValues = {"OPEN", "CLOSED", "CANCELLED"})
    private String status;
    
    @Schema(description = "안전도 검색 (조회용)", example = "SAFE", allowableValues = {"SAFE", "MODERATE", "CAUTION"})
    private String safetyLevel;
    
    // 실제 필터링이 동작하는 필드들
    @Schema(description = "최대 거리 필터 (km) - 실제 필터링 동작", example = "10.0")
    private Double maxDistance;
    
    @Schema(description = "최소 페이스 필터 (이 페이스보다 빠르거나 같은 크루들) - 실제 필터링 동작", example = "6'00\"/km")
    private String minPace;
    
    @Schema(description = "시작 시간 이후 필터 - 실제 필터링 동작", example = "2025-01-05T16:00:00")
    private LocalDateTime startTimeFrom;
    
    // 페이징 및 정렬
    @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    private Integer page = 0;
    
    @Schema(description = "페이지 크기", example = "20")
    private Integer size = 20;
    
    @Schema(description = "정렬 기준", example = "createdAt", allowableValues = {"createdAt", "participants", "distance", "pace", "startTime"})
    private String sortBy = "createdAt";
    
    @Schema(description = "정렬 방향", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortDirection = "DESC";
    
    @Schema(description = "정렬 타입", example = "latest", allowableValues = {"popular", "latest", "distance", "pace", "time"})
    private String sortType;
}
