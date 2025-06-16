package com.seoultech.ecc.review.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.report.datamodel.ReportEntity;
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
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    @Column(nullable = false)
    private int week;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_uuid", nullable = false)
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.NOT_READY;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String contents;

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewTestEntity reviewTest;

    // 편의 메서드
    public String getReportId() {
        return report != null ? report.getId().toString() : null;
    }

    public Integer getMemberId() {
        return member != null ? member.getUuid() : null;
    }

    public String getMemberName() {
        return member != null ? member.getName() : null;
    }
}