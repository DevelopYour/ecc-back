package com.seoultech.ecc.team.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import com.seoultech.ecc.study.datamodel.TimeEntity;
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
    private Long teamId;

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

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int semester;

    @Column(name = "is_regular", columnDefinition = "boolean default false")
    private boolean isRegular;

    @Column(name = "study_count", nullable = false, columnDefinition = "int default 0")
    private int studyCount;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> teamMembers = new ArrayList<>();
}