package com.seoultech.ecc.study.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.report.datamodel.ReportDocument;
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
@Table(name = "review")
public class ReviewEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(name = "report_id", nullable = false)
    private String reportId;

    @ManyToOne
    @JoinColumn(name = "member_uuid", nullable = false)
    private MemberEntity member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
}
