package com.kbsw.seasonthon.running.entity;

import com.kbsw.seasonthon.global.base.domain.BaseEntity;
import com.kbsw.seasonthon.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "running_records")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RunningRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double distanceKm;  // 총 거리 (km)

    @Column(nullable = false)
    private Integer durationMinutes;  // 총 시간 (분)

    @Column(nullable = false)
    private String pace;  // 평균 페이스 (예: "6'30\"/km")

    @Column
    private String bestPace;  // 최고 페이스 (예: "5'45\"/km")

    @Column
    private LocalDateTime startTime;  // 러닝 시작 시간

    @Column
    private LocalDateTime endTime;  // 러닝 종료 시간

    @Column(columnDefinition = "TEXT")
    private String routeData;  // 경로 데이터 (JSON 형태)

    @Column
    private String weather;  // 날씨 정보

    @Column
    private String notes;  // 메모

    // 페이스 계산 메서드
    public String calculatePace() {
        if (durationMinutes == 0 || distanceKm == 0) {
            return "0'00\"/km";
        }
        
        double pacePerKm = (double) durationMinutes / distanceKm;
        int minutes = (int) pacePerKm;
        int seconds = (int) ((pacePerKm - minutes) * 60);
        
        return String.format("%d'%02d\"/km", minutes, seconds);
    }

    // 최고 페이스 계산 (1km 기준)
    public String calculateBestPace() {
        if (bestPace != null && !bestPace.isEmpty()) {
            return bestPace;
        }
        return calculatePace();
    }
}
