package com.kbsw.seasonthon.crew.domain;

import com.kbsw.seasonthon.crew.enums.CrewStatus;
import com.kbsw.seasonthon.crew.enums.SafetyLevel;
import com.kbsw.seasonthon.global.base.domain.BaseEntity;
import com.kbsw.seasonthon.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "crews")
public class Crew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CrewStatus status = CrewStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private String routeId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Integer safetyScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SafetyLevel safetyLevel;

    @Column(nullable = false)
    private Integer durationMin;

    @Column
    private String startLocation;        // 시작 위치 (예: "경북대학교 정문")

    @Column
    private String pace;                 // 페이스 (예: "6'30\"/km")

    @Column
    private LocalDateTime startTime;     // 시작 시간

    @ElementCollection
    @CollectionTable(name = "crew_waypoints", joinColumns = @JoinColumn(name = "crew_id"))
    @Column(name = "waypoint")
    @Builder.Default
    private List<String> waypoints = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "crew_tags", joinColumns = @JoinColumn(name = "crew_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CrewParticipant> participants = new ArrayList<>();

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void updateWaypoints(List<String> waypoints) {
        this.waypoints.clear();
        this.waypoints.addAll(waypoints);
    }

    public void updateTags(List<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public void updateStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void updatePace(String pace) {
        this.pace = pace;
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void updateStatus(CrewStatus status) {
        this.status = status;
    }

    public void updateRouteInfo(String routeId, String type, Double distanceKm, 
                               Integer safetyScore, Integer durationMin, List<String> waypoints) {
        this.routeId = routeId;
        this.type = type;
        this.distanceKm = distanceKm;
        this.safetyScore = safetyScore;
        this.safetyLevel = SafetyLevel.fromScore(safetyScore);
        this.durationMin = durationMin;
        this.waypoints.clear();
        this.waypoints.addAll(waypoints);
    }

    public boolean isHost(User user) {
        return this.host.getId().equals(user.getId());
    }

    public boolean canEdit(User user) {
        return isHost(user) || user.getRole().name().equals("ADMIN");
    }

    public boolean canDelete(User user) {
        return isHost(user) || user.getRole().name().equals("ADMIN");
    }
}


