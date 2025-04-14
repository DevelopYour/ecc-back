package com.seoultech.ecc.study.datamodel;

import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.global.BaseEntity;
import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.team.datamodel.SubjectEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study")
public class StudyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StudyStatus status;
}
