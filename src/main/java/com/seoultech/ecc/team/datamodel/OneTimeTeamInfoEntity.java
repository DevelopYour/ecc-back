package com.seoultech.ecc.team.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "one_time_team_info")
public class OneTimeTeamInfoEntity extends BaseEntity {

    @Id
    @Column(name = "team_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_members", nullable = false, columnDefinition = "int default 5")
    private int maxMembers;

    @Column(name = "min_members", nullable = false, columnDefinition = "int default 2")
    private int minMembers;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OneTimeTeamStatus status;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;

    // 편의 메서드: 번개 스터디 신청 가능 여부 체크
    public boolean isApplicable() {
        LocalDateTime now = LocalDateTime.now();
        return status == OneTimeTeamStatus.RECRUITING &&
                team.getTeamMembers().size() < maxMembers &&
                now.isBefore(startTime);
    }

    // 편의 메서드: 번개 스터디 취소 가능 여부 체크 (시작 3시간 전까지만 취소 가능)
    public boolean isCancelable() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancelDeadline = startTime.minusHours(3);
        return (status == OneTimeTeamStatus.RECRUITING || status == OneTimeTeamStatus.UPCOMING) &&
                now.isBefore(cancelDeadline);
    }

    // 편의 메서드: 번개 스터디 상태 업데이트
    public void updateStatus() {
        if (status == OneTimeTeamStatus.CANCELED) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(endTime)) {
            status = OneTimeTeamStatus.COMPLETED;
        } else if (now.isAfter(startTime)) {
            status = OneTimeTeamStatus.IN_PROGRESS;
        } else if (team.getTeamMembers().size() >= minMembers) {
            status = OneTimeTeamStatus.UPCOMING;
        }
    }
}