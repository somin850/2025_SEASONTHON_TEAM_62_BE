package com.kbsw.seasonthon.report.entity;

import com.kbsw.seasonthon.global.base.domain.BaseEntity;
import com.kbsw.seasonthon.report.enums.ReportStatus;
import com.kbsw.seasonthon.report.enums.TargetType;
import com.kbsw.seasonthon.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 24)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private ReportStatus status = ReportStatus.OPEN;

    public void updateStatus(ReportStatus status) {
        this.status = status;
    }
}
