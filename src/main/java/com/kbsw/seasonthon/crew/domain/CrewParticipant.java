package com.kbsw.seasonthon.crew.domain;

import com.kbsw.seasonthon.crew.enums.ParticipantStatus;
import com.kbsw.seasonthon.global.base.domain.BaseEntity;
import com.kbsw.seasonthon.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "crew_participants")
public class CrewParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.APPLIED;

    public void approve() {
        this.status = ParticipantStatus.APPROVED;
    }

    public void reject() {
        this.status = ParticipantStatus.REJECTED;
    }

    public boolean isApproved() {
        return this.status == ParticipantStatus.APPROVED;
    }

    public boolean isApplied() {
        return this.status == ParticipantStatus.APPLIED;
    }
}


