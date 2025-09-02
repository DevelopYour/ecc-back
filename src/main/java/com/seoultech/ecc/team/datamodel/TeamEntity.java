package com.seoultech.ecc.team.datamodel;

import com.seoultech.ecc.admin.datamodel.SemesterEntity;
import com.seoultech.ecc.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team")
public class TeamEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private TimeEntity time;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private SemesterEntity semesterEntity;

    @Column(name = "is_regular", nullable = false, columnDefinition = "boolean default true")
    private boolean regular = true;

    @Column(name = "study_count", nullable = false, columnDefinition = "int default 0")
    private int studyCount;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> teamMembers = new ArrayList<>();

    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private OneTimeTeamInfoEntity oneTimeInfo;

    // 편의 메서드: 팀 멤버 수 반환
    public int getMemberCount() {
        return teamMembers.size();
    }

    // 편의 메서드: 번개 스터디 여부 확인
    public boolean isOneTimeTeam() {
        return !regular;
    }

    // 편의 메서드: 번개 스터디 상태 업데이트
    public void updateOneTimeTeamStatus() {
        if (isOneTimeTeam() && oneTimeInfo != null) {
            oneTimeInfo.updateStatus();
        }
    }
}