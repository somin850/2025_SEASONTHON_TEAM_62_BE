package com.kbsw.seasonthon.favorite.entity;

import com.kbsw.seasonthon.global.base.domain.BaseEntity;
import com.kbsw.seasonthon.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "favorites")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @ElementCollection
    @CollectionTable(name = "favorite_waypoints", joinColumns = @JoinColumn(name = "favorite_id"))
    @Column(name = "waypoint")
    @Builder.Default
    private List<String> waypoints = List.of();

    @Column(columnDefinition = "TEXT")
    private String savedPolyline;

    @Column
    private Integer distanceM;

    @Column
    private Integer durationS;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateWaypoints(List<String> waypoints) {
        this.waypoints = waypoints;
    }

    public void updateSavedPolyline(String savedPolyline) {
        this.savedPolyline = savedPolyline;
    }

    public void updateDistanceM(Integer distanceM) {
        this.distanceM = distanceM;
    }

    public void updateDurationS(Integer durationS) {
        this.durationS = durationS;
    }
}
