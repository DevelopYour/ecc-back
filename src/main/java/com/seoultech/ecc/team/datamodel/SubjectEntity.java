package com.seoultech.ecc.team.datamodel;

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
@Table(name = "subject")
public class SubjectEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subjectId; // Long -> Integer 변경

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String template;

    @Column(nullable = false)
    private String prompt;
}