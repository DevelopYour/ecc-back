package com.seoultech.ecc.admin.datamodel;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team_recruitment_status")
public class TeamRecruitmentStatusEntity {

    @Id
    private Integer id = 1; // 단일 엔티티 -> 항상 1로 고정

    @Column(name = "is_recruiting", nullable = false, columnDefinition = "boolean default false")
    private Boolean recruiting;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false, unique = true)
    private SemesterEntity semester;

    // 편의 메서드: 모집 상태 토글
    public void toggleRecruiting() {
        this.recruiting = !this.recruiting;
    }
}
