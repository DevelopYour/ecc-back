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
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uuid; // 회원ID(학번)

    @Column(name = "kakao_uuid", nullable = false, unique = true)
    private Integer kakaoUuid; // 카카오로그인ID

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String tel;

    @Column(name = "kakao_tel", nullable = false)
    private String kakaoTel; // 카카오아이디

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Double rate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", nullable = false)
    private MajorEntity major;

}
