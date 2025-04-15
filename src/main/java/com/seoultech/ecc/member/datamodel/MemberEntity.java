package com.seoultech.ecc.member.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import com.seoultech.ecc.team.datamodel.TeamMemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "kakao_tel")
    private String kakaoTel; // 카카오톡 아이디

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

    // RefreshToken 관련 필드
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    // RefreshToken 만료 확인 메서드
    public boolean isRefreshTokenExpired() {
        return refreshTokenExpiresAt != null && LocalDateTime.now().isAfter(refreshTokenExpiresAt);
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> teamMembers = new ArrayList<>();

    // 사용자가 특정 상태인지 확인하는 편의 메서드들
    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public boolean isPending() {
        return this.status == MemberStatus.PENDING;
    }

    public boolean isSuspended() {
        return this.status == MemberStatus.SUSPENDED;
    }

    public boolean isBanned() {
        return this.status == MemberStatus.BANNED;
    }

    public boolean isWithdrawn() {
        return this.status == MemberStatus.WITHDRAWN;
    }

    public boolean isDormant() {
        return this.status == MemberStatus.DORMANT;
    }

    public boolean isDormantRequested() {
        return this.status == MemberStatus.DORMANT_REQUESTED;
    }

    // Major 정보를 안전하게 가져오는 편의 메서드
    public Long getMajorId() {
        return major != null ? major.getId() : null;
    }

    public String getMajorName() {
        return major != null ? major.getName() : null;
    }
}