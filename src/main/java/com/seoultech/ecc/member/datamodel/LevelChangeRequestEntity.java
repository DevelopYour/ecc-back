package com.seoultech.ecc.member.datamodel;

import com.seoultech.ecc.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "level_change_request")
public class LevelChangeRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_uuid", nullable = false)
    private MemberEntity member;

    @Column(nullable = false)
    private int currentLevel; // 요청 시점 레벨 기록

    @Column(nullable = false)
    private int requestedLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(columnDefinition = "TEXT")
    private String reason;

    public enum RequestStatus {
        PENDING,   // 승인 대기 중
        APPROVED,  // 승인됨
        REJECTED   // 거절됨
    }
}