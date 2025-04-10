package com.seoultech.ecc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uuid; // 회원ID

    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId; // 학번 (로그인 ID)로 사용

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String tel; // 전화번호 (초기 비밀번호)

    @Column(name = "kakao_id")
    private String kakaoId; // 카카오톡 아이디

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

    @Column(columnDefinition = "TEXT")
    private String motivation; // 지원 동기

    // RefreshToken 관련 필드 추가
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    // RefreshToken 만료 확인 메서드
    public boolean isRefreshTokenExpired() {
        return refreshTokenExpiresAt != null && LocalDateTime.now().isAfter(refreshTokenExpiresAt);
    }

    /* 카카오 로그인 보류
    @Column(name = "kakao_uuid", nullable = false, unique = true)
    private Integer kakaoUuid; // 카카오로그인ID
     */
}