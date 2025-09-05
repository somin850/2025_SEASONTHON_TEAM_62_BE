package com.kbsw.seasonthon.running.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RunningStatsResponse {
    
    private Integer totalRuns;           // 총 러닝 횟수
    private Double totalDistanceKm;      // 총 거리 (km)
    private Integer totalDurationMinutes; // 총 러닝 시간 (분)
    private String averagePace;          // 평균 페이스
    private String bestPace;             // 최고 페이스
    private Double averageDistanceKm;    // 평균 거리 (km)
    private Integer averageDurationMinutes; // 평균 러닝 시간 (분)
    private LocalDateTime lastRunDate;   // 마지막 러닝 날짜
    private List<RecentRun> recentRuns;  // 최근 러닝 기록 (최대 5개)

    @Getter
    @Builder
    public static class RecentRun {
        private Long id;
        private Double distanceKm;
        private Integer durationMinutes;
        private String pace;
        private LocalDateTime startTime;
        private String weather;
    }
}
