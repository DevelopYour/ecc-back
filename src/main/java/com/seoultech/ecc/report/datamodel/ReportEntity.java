package com.seoultech.ecc.report.datamodel;

import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.global.BaseEntity;
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
@Table(name = "report")
public class ReportEntity extends BaseEntity {
    // TODO: Document로 바꿀 예정이므로 조인관계 모두 id로 처리하고 contents에 JSON 형식으로 데이터 저장함
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private Long subjectId;

    @Column(nullable = false)
    private int week;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Column(nullable = false)
    private int grade;

    @Column(nullable = false)
    private boolean isSubmitted;
}
