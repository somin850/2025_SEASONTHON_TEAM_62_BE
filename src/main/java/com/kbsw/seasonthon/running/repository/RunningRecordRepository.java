package com.kbsw.seasonthon.running.repository;

import com.kbsw.seasonthon.running.entity.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {

    /**
     * 특정 사용자의 러닝 기록 조회 (최신순)
     */
    @Query("SELECT r FROM RunningRecord r WHERE r.user.id = :userId ORDER BY r.startTime DESC")
    List<RunningRecord> findByUserOrderByStartTimeDesc(@Param("userId") Long userId);

    /**
     * 특정 사용자의 최근 러닝 기록 조회 (최대 5개)
     */
    @Query("SELECT r FROM RunningRecord r WHERE r.user.id = :userId ORDER BY r.startTime DESC")
    List<RunningRecord> findTop5ByUserOrderByStartTimeDesc(@Param("userId") Long userId);

    /**
     * 특정 사용자의 총 러닝 횟수
     */
    @Query("SELECT COUNT(r) FROM RunningRecord r WHERE r.user.id = :userId")
    long countByUser(@Param("userId") Long userId);

    /**
     * 특정 사용자의 총 거리 합계
     */
    @Query("SELECT COALESCE(SUM(r.distanceKm), 0) FROM RunningRecord r WHERE r.user.id = :userId")
    Double sumDistanceByUser(@Param("userId") Long userId);

    /**
     * 특정 사용자의 총 러닝 시간 합계
     */
    @Query("SELECT COALESCE(SUM(r.durationMinutes), 0) FROM RunningRecord r WHERE r.user.id = :userId")
    Integer sumDurationByUser(@Param("userId") Long userId);

    /**
     * 특정 사용자의 평균 페이스 계산
     */
    @Query("SELECT COALESCE(AVG(r.durationMinutes / r.distanceKm), 0) FROM RunningRecord r WHERE r.user.id = :userId")
    Double averagePaceByUser(@Param("userId") Long userId);

    /**
     * 특정 사용자의 최고 페이스 (가장 빠른 페이스)
     */
    @Query("SELECT MIN(r.durationMinutes / r.distanceKm) FROM RunningRecord r WHERE r.user.id = :userId")
    Double bestPaceByUser(@Param("userId") Long userId);

    /**
     * 특정 사용자의 마지막 러닝 날짜
     */
    @Query("SELECT MAX(r.startTime) FROM RunningRecord r WHERE r.user.id = :userId")
    LocalDateTime lastRunDateByUser(@Param("userId") Long userId);

    /**
     * 특정 기간의 러닝 기록 조회
     */
    @Query("SELECT r FROM RunningRecord r WHERE r.user.id = :userId AND r.startTime BETWEEN :startDate AND :endDate ORDER BY r.startTime DESC")
    List<RunningRecord> findByUserAndDateRange(@Param("userId") Long userId, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
}
