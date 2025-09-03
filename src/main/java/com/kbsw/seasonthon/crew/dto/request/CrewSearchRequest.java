package com.kbsw.seasonthon.crew.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CrewSearchRequest {
    private String keyword;           // 제목, 설명 검색
    private String startLocation;     // 시작 위치 검색
    private List<String> tags;       // 태그 필터
    private String status;           // 크루 상태 필터
    private String safetyLevel;      // 안전도 필터
    private Double minDistance;      // 최소 거리 (km)
    private Double maxDistance;      // 최대 거리 (km)
    private String minPace;          // 최소 페이스 (예: "5'00\"/km")
    private String maxPace;          // 최대 페이스 (예: "8'00\"/km")
    private LocalDateTime startTimeFrom; // 시작 시간 범위 (이후)
    private LocalDateTime startTimeTo;   // 시작 시간 범위 (이전)
    private Integer minDuration;     // 최소 소요시간
    private Integer maxDuration;     // 최대 소요시간
    private Integer page = 0;        // 페이지 번호 (0부터 시작)
    private Integer size = 20;       // 페이지 크기
    private String sortBy = "createdAt"; // 정렬 기준 (createdAt, participants, distance, pace, startTime)
    private String sortDirection = "DESC"; // 정렬 방향 (ASC, DESC)
    private String sortType;         // 정렬 타입 (popular, latest, distance, pace, time)
}
