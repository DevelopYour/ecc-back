package com.seoultech.ecc.report.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.datamodel.TeamEntity;
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
@Table(name = "report")
public class ReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportMemberEntity> reportMembers = new ArrayList<>();

    @Column(nullable = false)
    private int week;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int grade;

    @Column(name = "is_submitted", nullable = false, columnDefinition = "boolean default false")
    private boolean isSubmitted;

    // 편의 메서드
    public Integer getTeamId() {
        return team != null ? team.getTeamId().intValue() : null;
    }

    public Integer getSubjectId() {
        return subject != null ? subject.getSubjectId().intValue() : null;
    }
}