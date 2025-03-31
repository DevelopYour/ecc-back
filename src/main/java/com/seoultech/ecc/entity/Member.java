package com.seoultech.ecc.entity;

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
@Table
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @Column(nullable = false, unique = true)
    private String kakaoUuid;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    private String tel;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String studentNo;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private double rate;

    @Column(nullable = false)
    private MemberStatus status;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}

