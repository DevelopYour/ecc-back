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

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

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
        // 이미 취소된 상태는 변경하지 않음
        if (status == OneTimeTeamStatus.CANCELED) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 종료 시간이 지났으면 완료 상태로 변경
        if (now.isAfter(endTime)) {
            status = OneTimeTeamStatus.COMPLETED;
        }
        // 시작 시간이 지났으면 진행 중 상태로 변경
        else if (now.isAfter(startTime)) {
            status = OneTimeTeamStatus.IN_PROGRESS;
        }
        // 시작 전이라면, 인원수에 따라 상태 변경
        else {
            int currentMembers = team.getTeamMembers().size();

            // 최대 인원이 충족되었으면 UPCOMING으로 변경
            if (currentMembers >= maxMembers) {
                status = OneTimeTeamStatus.UPCOMING;
            }
            // 최소 인원보다 적으면 항상 RECRUITING으로 변경 (기존 상태가 UPCOMING이더라도)
            else if (currentMembers < minMembers) {
                status = OneTimeTeamStatus.RECRUITING;
            }
            // 그 외(최소 인원은 충족, 최대 인원은 아직)는 RECRUITING 유지
            else {
                status = OneTimeTeamStatus.RECRUITING;
            }
        }
    }
}