package com.kbsw.seasonthon.running.service;

import com.kbsw.seasonthon.running.dto.response.RunningStatsResponse;
import com.kbsw.seasonthon.running.entity.RunningRecord;
import com.kbsw.seasonthon.running.repository.RunningRecordRepository;
import com.kbsw.seasonthon.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RunningRecordService {

    private final RunningRecordRepository runningRecordRepository;

    /**
     * 사용자의 러닝 통계 조회
     */
    public RunningStatsResponse getRunningStats(User user) {
        Long userId = user.getId();
        
        // 기본 통계 조회
        long totalRuns = runningRecordRepository.countByUser(userId);
        Double totalDistanceKm = runningRecordRepository.sumDistanceByUser(userId);
        Integer totalDurationMinutes = runningRecordRepository.sumDurationByUser(userId);
        Double averagePace = runningRecordRepository.averagePaceByUser(userId);
        Double bestPace = runningRecordRepository.bestPaceByUser(userId);
        LocalDateTime lastRunDate = runningRecordRepository.lastRunDateByUser(userId);
        
        // 최근 러닝 기록 조회 (최대 5개)
        List<RunningRecord> recentRecords = runningRecordRepository.findTop5ByUserOrderByStartTimeDesc(userId);
        
        // 평균 거리와 시간 계산
        Double averageDistanceKm = totalRuns > 0 ? totalDistanceKm / totalRuns : 0.0;
        Integer averageDurationMinutes = totalRuns > 0 ? totalDurationMinutes / (int) totalRuns : 0;
        
        // 페이스 포맷팅
        String averagePaceFormatted = formatPace(averagePace);
        String bestPaceFormatted = formatPace(bestPace);
        
        // 최근 러닝 기록 변환
        List<RunningStatsResponse.RecentRun> recentRuns = recentRecords.stream()
                .map(record -> RunningStatsResponse.RecentRun.builder()
                        .id(record.getId())
                        .distanceKm(record.getDistanceKm())
                        .durationMinutes(record.getDurationMinutes())
                        .pace(record.getPace())
                        .startTime(record.getStartTime())
                        .weather(record.getWeather())
                        .build())
                .collect(Collectors.toList());
        
        return RunningStatsResponse.builder()
                .totalRuns((int) totalRuns)
                .totalDistanceKm(totalDistanceKm != null ? totalDistanceKm : 0.0)
                .totalDurationMinutes(totalDurationMinutes != null ? totalDurationMinutes : 0)
                .averagePace(averagePaceFormatted)
                .bestPace(bestPaceFormatted)
                .averageDistanceKm(averageDistanceKm)
                .averageDurationMinutes(averageDurationMinutes)
                .lastRunDate(lastRunDate)
                .recentRuns(recentRuns)
                .build();
    }

    /**
     * 페이스 포맷팅 (분/km -> '분'초"/km)
     */
    private String formatPace(Double paceMinutes) {
        if (paceMinutes == null || paceMinutes == 0) {
            return "0'00\"/km";
        }
        
        int minutes = (int) paceMinutes.doubleValue();
        int seconds = (int) ((paceMinutes - minutes) * 60);
        
        return String.format("%d'%02d\"/km", minutes, seconds);
    }

    /**
     * 러닝 기록 저장
     */
    @Transactional
    public RunningRecord saveRunningRecord(RunningRecord record) {
        // 페이스 자동 계산
        String calculatedPace = record.calculatePace();
        record = RunningRecord.builder()
                .id(record.getId())
                .user(record.getUser())
                .distanceKm(record.getDistanceKm())
                .durationMinutes(record.getDurationMinutes())
                .pace(calculatedPace)
                .bestPace(record.getBestPace())
                .startTime(record.getStartTime())
                .endTime(record.getEndTime())
                .routeData(record.getRouteData())
                .weather(record.getWeather())
                .notes(record.getNotes())
                .build();
        
        return runningRecordRepository.save(record);
    }

    /**
     * 사용자의 모든 러닝 기록 조회
     */
    public List<RunningRecord> getUserRunningRecords(Long userId) {
        return runningRecordRepository.findByUserOrderByStartTimeDesc(userId);
    }
}
