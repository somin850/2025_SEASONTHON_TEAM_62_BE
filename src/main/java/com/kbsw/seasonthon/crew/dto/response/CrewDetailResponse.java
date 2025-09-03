package com.kbsw.seasonthon.crew.dto.response;

import com.kbsw.seasonthon.crew.enums.CrewStatus;
import com.kbsw.seasonthon.crew.enums.SafetyLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CrewDetailResponse {
    private Long id;
    private String title;
    private String description;
    private CrewStatus status;
    private String hostName;
    private String hostEmail;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String routeId;
    private String type;
    private Double distanceKm;
    private Integer safetyScore;
    private SafetyLevel safetyLevel;
    private Integer durationMin;
    private List<String> waypoints;
    private List<String> tags;
    private String startLocation;        // 시작 위치 (예: "경북대학교 정문")
    private String pace;                 // 페이스 (예: "6'30\"/km")
    private LocalDateTime startTime;     // 시작 시간 (예: "18:00")
    private List<ParticipantInfo> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class ParticipantInfo {
        private Long userId;
        private String userName;
        private String userEmail;
        private String status;
        private LocalDateTime appliedAt;
    }
}
